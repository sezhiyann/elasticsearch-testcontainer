package org.example;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.example.elasticsearch.ElasticsearchDataLoader;
import org.example.mongo.MongodbDataLoader;
import org.springframework.test.context.TestExecutionListeners;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestExecutionListeners(listeners = {MongodbDataLoader.class,
    ElasticsearchDataLoader.class}, mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS)
public @interface EnableDataLoaders {

}
