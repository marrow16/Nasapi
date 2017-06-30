package com.adeptions.annotations;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("PROPFIND")
@Documented
public @interface PROPFIND {
}
