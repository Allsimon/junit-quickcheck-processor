package com.allsimon.quickcheck.processor.model;

import com.allsimon.quickcheck.Generator;

@Generator(ValidPojo.class)
@Generator(PojoTuple.class)
@Generator(SimplePojo.class)
public interface Generators {
}
