package com.github.allsimon.quickcheck.processor.model;

import com.github.allsimon.quickcheck.Generator;

@Generator(ValidPojo.class)
@Generator(PojoTuple.class)
@Generator(SimplePojo.class)
public interface Generators {
}
