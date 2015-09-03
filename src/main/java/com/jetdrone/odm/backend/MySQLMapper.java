//package com.jetdrone.odm.backend;
//
//import com.jetdrone.odm.Mapper;
//import com.jetdrone.odm.Record;
//import io.vertx.core.AsyncResultHandler;
//import io.vertx.core.eventbus.EventBus;
//
//import java.util.List;
//
//public abstract class MySQLMapper<R extends Record<String>> extends Mapper<String, R, String> {
//
//    private static final String ALL = "*";
//
//    public MySQLMapper(EventBus eventBus, String address, String entity) {
//        super(eventBus, address, entity, "id");
//    }
//
//    public MySQLMapper(EventBus eventBus, String address, String entity, String id) {
//        super(eventBus, address, entity, id);
//    }
//
//    @Override
//    public void find(final String query, final AsyncResultHandler<List<R>> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (List<R>) null));
//    }
//
//    @Override
//    public void findOne(String query, AsyncResultHandler<R> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (R) null));
//    }
//
//    @Override
//    public void findAll(AsyncResultHandler<List<R>> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (List<R>) null));
//    }
//
//    @Override
//    public void update(String query, R record, AsyncResultHandler<Void> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Void) null));
//    }
//
//    @Override
//    public void remove(String query, AsyncResultHandler<Void> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Void) null));
//    }
//
//    @Override
//    public void save(R record, AsyncResultHandler<String> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (String) null));
//    }
//
//    @Override
//    public void findById(String id, AsyncResultHandler<R> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (R) null));
//    }
//
//    @Override
//    public void update(R record, AsyncResultHandler<Void> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Void) null));
//    }
//
//    @Override
//    public void remove(R record, AsyncResultHandler<Void> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Void) null));
//    }
//
//    @Override
//    public void count(String query, AsyncResultHandler<Number> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Number) null));
//    }
//
//    @Override
//    public void count(AsyncResultHandler<Number> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Number) null));
//    }
//
//    @Override
//    public void truncate(AsyncResultHandler<Void> callback) {
//        callback.handle(wrapResult(new UnsupportedOperationException(), (Void) null));
//    }
//}
