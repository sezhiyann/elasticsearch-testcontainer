package org.example;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestIndex {
  
  private String id;
  private String productUniqueCode;
  private String year;
  private String name;
  private String code;

}
