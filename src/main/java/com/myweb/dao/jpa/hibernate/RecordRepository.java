package com.myweb.dao.jpa.hibernate;

import com.myweb.pojo.Bookstore;
import com.myweb.pojo.Record;
import com.myweb.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryDefinition(domainClass = Record.class, idClass = Integer.class)
public interface RecordRepository extends JpaRepository<Record,Integer> {

    @Query("select record from Record record where record.user.id = ?1")
    public List<Record> findAllByUserid(int userid);

    @Query("select record from Record record where record.user.id = ?1 and record.status = ?2")
    public List<Record> findAllByUseridAndStatus(int userid,int status);

    @Query("select record from Record record where record.bookstore.owner.id = ?1")
    public List<Record> findAllByOwnerid(int ownerid);

    @Query("select record from Record record where record.bookstore.owner.id = ?1 and record.status = ?2")
    public List<Record> findAllByOwneridAndStatus(int ownerid,int status);
}