package com.ecommerce.inditex.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Product {

    private long id;

    private int sequence;
}
