package com.adeptions.rsextensions;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("VIEW")
@Documented
public @interface VIEW {
}
