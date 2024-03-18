package com.aesctzn.microservices.starter.temporal.services.namespaces;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import com.google.protobuf.Duration;

@Getter
@Setter
public class NamespaceRegisterDto implements Serializable  {

    private String name;
    private String description;
    private int retention;
    private String emailOwner;
    private String historyArchivalURI;
    private String visibilityArchivalURI;

}
