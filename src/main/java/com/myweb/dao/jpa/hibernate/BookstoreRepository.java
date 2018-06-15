package com.myweb.dao.jpa.hibernate;

import com.myweb.pojo.Bookstore;
import com.myweb.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.RepositoryDefinition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryDefinition(domainClass = Bookstore.class, idClass = Integer.class)
public interface BookstoreRepository extends JpaRepository<Bookstore, Integer> {

    @Query("select bookstore from Bookstore bookstore where bookstore.user.id = ?1 and bookstore.status = ?2")
    public List<Bookstore> findAllByUseridAndStatus(int userid,int status);

    @Query("select bookstore from Bookstore bookstore where bookstore.owner.id = ?1 and bookstore.status = ?2")
    public List<Bookstore> findAllByOwneridAndStatus(int owerid,int status);

    @Query("select bookstore from Bookstore bookstore where bookstore.owner.id = ?1 or bookstore.user.id = ?2")
    public List<Bookstore> findAllByOwneridOrUserid(int owerid, int userid);

    @Query("select bookstore from Bookstore bookstore where bookstore.book.id = ?1 and bookstore.owner.id = ?2 and bookstore.status = ?3")
    public List<Bookstore> findAllByBookidAndOwneridAndStatus(int bookid, int ownerid,int status);

    @Query("select bookstore from Bookstore bookstore where bookstore.book.id in (?1) and bookstore.owner.id = ?2 and bookstore.status = ?3")
    public List<Bookstore> findAllByBookidsAndOwneridAndStatus( List<Integer> bookids, int ownerid,int status);

}