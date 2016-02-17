package com.avaje.ebeanservice.elastic;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.Query;
import com.avaje.ebean.QueryEachConsumer;
import com.avaje.ebean.bean.EntityBean;
import com.avaje.ebean.text.PathProperties;
import com.avaje.ebeaninternal.server.deploy.BeanDescriptor;
import com.avaje.ebeaninternal.server.deploy.BeanProperty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rob on 17/02/16.
 */
public class NestedDocUpdate {

  final EbeanServer server;
  final BeanDescriptor<?> desc;
  final ElasticBatchUpdate txn;
  final ElasticUpdateGroup.Nested nested;

  final Map<Object, String> jsonMap = new HashMap<Object,String>();

  final String path;
  final BeanDescriptor<?> targetDesc;

  public NestedDocUpdate(EbeanServer server, BeanDescriptor<?> desc, ElasticBatchUpdate txn, ElasticUpdateGroup.Nested nested) {
    this.server = server;
    this.desc = desc;
    this.txn = txn;
    this.nested = nested;

    this.path = nested.getPath();
    this.targetDesc = desc.getBeanDescriptor(path);
  }

  public void process() throws IOException {

    // customer

    // customer Ids
    List<Object> nestedIds = nested.getIds();

    PathProperties nestedDoc = desc.docStoreNested(path);

    fetchEmbedded(targetDesc, nestedIds, nestedDoc);

  }

  private <T> void fetchEmbedded(BeanDescriptor<T> targetDesc, List<Object> nestedIds, PathProperties nestedDoc) throws IOException {

    Query<T> pathQuery = server.createQuery(targetDesc.getBeanType());
    pathQuery.apply(nestedDoc);
    pathQuery.where().idIn(nestedIds);

    List<T> list = pathQuery.findList();
    for (T bean : list) {
      String embedJson = server.json().toJson(bean, nestedDoc);
      Object beanId = targetDesc.getBeanId(bean);
      jsonMap.put(beanId, embedJson);
    }

    // fetch the ids of the top level bean

    processTop(desc, nestedIds);

  }

  private <T> void processTop(final BeanDescriptor<T> beanDesc, List<Object> nestedIds) {

    Query<T> topQuery = server.createQuery(beanDesc.getBeanType());
    topQuery.select("id,"+nested.getPath());
    topQuery.where().in(nested.getPath()+".id", nestedIds);

    final BeanProperty property = beanDesc.getBeanProperty(nested.getPath());


    topQuery.findEach(new QueryEachConsumer<T>() {
      @Override
      public void accept(T bean)  {

        try {
          Object beanId = beanDesc.getBeanId(bean);
          Object embBean = property.getValue((EntityBean) bean);
          Object targetId = targetDesc.getId((EntityBean) embBean);

          String json = jsonMap.get(targetId);

          desc.docStoreUpdateEmbedded(beanId, path, json, txn.obtain());

          System.out.println("send it beanId:" + beanId + " json:" + json);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

  }
}
