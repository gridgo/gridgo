package io.gridgo.boot.data.test.data;

import java.util.List;

import org.joo.promise4j.Promise;

import io.gridgo.boot.data.support.annotations.DataAccess;
import io.gridgo.boot.data.support.annotations.JdbcProduce;
import io.gridgo.boot.data.support.impl.JdbcDataAccessHandler;
import io.gridgo.framework.support.Message;

@DataAccess(gateway = "mysql", handler = JdbcDataAccessHandler.class)
public interface UserDAO {

    @JdbcProduce("drop table if exists test_users")
    public Promise<Message, Exception> dropTable();

    @JdbcProduce("create table test_users (id int primary key, name varchar(255))")
    public Promise<Message, Exception> createTable();

    @JdbcProduce("insert into test_users (id, name) values (:1, :2)")
    public Promise<Message, Exception> add(int id, String name);

    @JdbcProduce(value = "select * from test_users where id = :1", pojo = User.class)
    public Promise<List<User>, Exception> find(int id);
}
