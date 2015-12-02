package com.avaje.tests.model.softdelete;

import javax.persistence.Entity;

@Entity
public class ESoftDelDown extends BaseSoftDelete {

  String down;

  public ESoftDelDown(String up) {
    this.down = down;
  }

  public String getDown() {
    return down;
  }

  public void setDown(String down) {
    this.down = down;
  }
}
