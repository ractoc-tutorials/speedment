package com.ractoc.tutorials.speedment.validators;

import javax.validation.constraints.Pattern;

@Pattern(regexp = "[a-fA-F0-9]{8}(-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}", message = "Not a valid UUID")
public @interface UuidPattern {
}
