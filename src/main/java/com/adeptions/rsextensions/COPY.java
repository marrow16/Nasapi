package com.adeptions.rsextensions;

import javax.ws.rs.HttpMethod;
import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("COPY")
@Documented
public @interface COPY {
}
