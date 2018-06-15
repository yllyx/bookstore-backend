package com.myweb.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myweb.dao.jpa.hibernate.*;
import com.myweb.pojo.Book;
import com.myweb.pojo.Bookstore;
import com.myweb.pojo.User;
import com.myweb.service.OneService;
import com.myweb.vo.DBBook;
import com.myweb.vo.OneParameter;
import com.utils.Result;
import com.utils.WeixinUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Service("OneService")
@SuppressWarnings("All")
@Transactional(readOnly = true)
public class OneServiceImpl implements OneService {

    private static final Logger logger = LogManager.getLogger(OneServiceImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookstoreRepository bookstoreRepository;

    @Autowired
    private RecordRepository recordRepository;

    @Override
    public Result scan(OneParameter oneParameter) {
        Result result = new Result();
        if (StringUtils.isBlank(oneParameter.getIsbn())) {
            result.setMessage("The required parameters are empty!");
            return result;
        }
        String url = "https://api.douban.com/v2/book/isbn/";
        String response = null;
        try {
            response = restTemplate.getForObject(url + oneParameter.getIsbn(), String.class);
        } catch (RestClientException e) {
            result.setMessage("Douban api has exception!");
            return result;
        }
        ObjectMapper mapper = new ObjectMapper();
        DBBook dbBook = null;
        try {
            dbBook = mapper.readValue(response, DBBook.class);
        } catch (IOException e) {
            e.printStackTrace();
            result.setMessage("Douban api result has exception!");
            return result;
        }
        String translators = "";
        for (String translator : dbBook.getTranslator()) {
            translators = translators + "," + translator;
        }
        Book book = new Book();
        if (StringUtils.isNotBlank(translators)) {
            book.setTranslator(translators.substring(1));
        }
        book.setIsbn(dbBook.getIsbn10() + "," + dbBook.getIsbn13());
        book.setTitle(dbBook.getTitle());
        book.setImage(dbBook.getImage());
        book.setPublisher(dbBook.getPublisher());
        if (StringUtils.isNotBlank(dbBook.getPrice())) {
            book.setPrice(new BigDecimal(dbBook.getPrice().substring(0, dbBook.getPrice().length() - 1)));
        }
        book.setRating(dbBook.getRating().getAverage());
        book.setSummary(dbBook.getSummary());
        result.setStatus(1);
        result.setData(book);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Result isbn(OneParameter oneParameter) {
        Result result = new Result();
        if (StringUtils.isBlank(oneParameter.getIsbn()) || oneParameter.getUserid() == 0) {
            result.setMessage("The required parameters are empty!");
            return result;
        }
        String url = "https://api.douban.com/v2/book/isbn/";
        String response = null;
        try {
            response = restTemplate.getForObject(url + oneParameter.getIsbn(), String.class);
        } catch (RestClientException e) {
            result.setMessage("从豆瓣获取图书信息失败!");
            return result;
        }
        ObjectMapper mapper = new ObjectMapper();
        DBBook dbBook = null;
        try {
            dbBook = mapper.readValue(response, DBBook.class);
        } catch (IOException e) {
            e.printStackTrace();
            result.setMessage("从豆瓣获取图书信息失败!");
            return result;
        }
        String authors = "";
        for (String author : dbBook.getAuthor()) {
            authors = authors + "," + author;
        }
        Book book = new Book();
        if (StringUtils.isNotBlank(authors)) {
            book.setAuthor(authors.substring(1));
        }
        String translators = "";
        for (String translator : dbBook.getTranslator()) {
            translators = translators + "," + translator;
        }
        if (StringUtils.isNotBlank(translators)) {
            book.setTranslator(translators.substring(1));
        }
        book.setIsbn(dbBook.getIsbn10() + "," + dbBook.getIsbn13());
        book.setTitle(dbBook.getTitle());
        book.setImage(dbBook.getImage());
        book.setPublisher(dbBook.getPublisher());
        if (StringUtils.isNotBlank(dbBook.getPrice())) {
            book.setPrice(new BigDecimal(dbBook.getPrice().substring(0, dbBook.getPrice().length() - 1)));
        }
        book.setRating(dbBook.getRating().getAverage());
        book.setSummary(dbBook.getSummary());
        bookRepository.save(book);
        Bookstore bookstore = new Bookstore();
        bookstore.setBook(book);
        bookstore.setOwner(userRepository.findOne(oneParameter.getUserid()));
        bookstore.setStatus(1);
        bookstoreRepository.save(bookstore);
        result.setStatus(1);
        result.setData(book);
        return result;
    }

    @Override
    public Result list(OneParameter oneParameter) {
        Result result = new Result();
        if (oneParameter.getOwnerid() == null || oneParameter.getOwnerid() == 0) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        List<Bookstore> bookstoreList = new ArrayList<>();
        if (oneParameter.getStatus() == null || oneParameter.getStatus() == 0) {
            bookstoreList = bookstoreRepository.findAllByOwneridOrUserid(oneParameter.getOwnerid(), oneParameter.getOwnerid());
        } else if(oneParameter.getStatus() == 1 || oneParameter.getStatus() == 2) {
            bookstoreList = bookstoreRepository.findAllByOwneridAndStatus(oneParameter.getOwnerid(), oneParameter.getStatus());
        }
        else if(oneParameter.getStatus() == 3) {
            bookstoreList = bookstoreRepository.findAllByUseridAndStatus(oneParameter.getOwnerid(), 2);
        }
        if (bookstoreList.size() == 0) {
            result.setMessage("你的书架为空!");
            return result;
        }
        for( int i = 0 ; i < bookstoreList.size() ; i ++){
            for(int t = 0 ; t < bookstoreList.get(i).getRecord().size() ; t ++){
                bookstoreList.get(i).getRecord().get(t).setBookstore(null);
            }
        }
        result.setStatus(1);
        result.setData(bookstoreList);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Result out(OneParameter oneParameter) {
        Result result = new Result();
        if (oneParameter.getOwnerid() == null || oneParameter.getOwnerid() == 0 || StringUtils.isBlank(oneParameter.getBookids())) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        List<Integer> bookids = new ArrayList<>();
        String[] ids = oneParameter.getBookids().split(",");
        for(String id : ids){
            bookids.add(Integer.parseInt(id));
        }
        List<Bookstore> bookstoreList = bookstoreRepository.findAllByBookidsAndOwneridAndStatus(bookids, oneParameter.getOwnerid(),1);
        if (bookstoreList.size() > 0) {
           bookstoreList.forEach(e->{
               bookstoreRepository.delete(e);
           });
            result.setStatus(1);
        }else{
            result.setMessage("未找到这本书,或这本书不是自有状态，不可下架！");
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Result weixinCode(String code) {
        Result result = new Result();
        if (StringUtils.isBlank(code)) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        String response = WeixinUtils.getOpenId(restTemplate, code);
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = mapper.readValue(response, User.class);
            if (StringUtils.isNotBlank(user.getOpenid())) {
                List<User> userList = userRepository.findByOpenid(user.getOpenid());
                if (userList.size() == 1) {
                    result.setStatus(1);
                    result.setData(userList.get(0));
                    return result;
                } else if (userList.size() == 0) {
                    userRepository.save(user);
                    result.setStatus(1);
                    result.setData(user);
                    return result;
                } else {
                    result.setMessage("openid存在重复记录");
                    return result;
                }
            } else {
                result.setMessage("授权失败，无法获取openid");
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Result regist(OneParameter oneParameter) {
        Result result = new Result();
        if (StringUtils.isBlank(oneParameter.getUsername()) || StringUtils.isBlank(oneParameter.getPassword()) || oneParameter.getId() != 0) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        if (userRepository.findByUsername(oneParameter.getUsername()).size() > 0) {
            result.setMessage("用户名已经被占用!");
            return result;
        }
        User user = new User();
        user.setUsername(oneParameter.getUsername());
        user.setPassword(oneParameter.getPassword());
        userRepository.save(user);
        result.setStatus(1);
        result.setData(user);
        return result;
    }

    @Override
    public Result login(OneParameter oneParameter) {
        Result result = new Result();
        if (StringUtils.isBlank(oneParameter.getUsername()) || StringUtils.isBlank(oneParameter.getPassword())) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        List<User> userList = userRepository.findByUsernameAndPassword(oneParameter.getUsername(), oneParameter.getPassword());
        if (userList.size() == 1) {
            result.setStatus(1);
            result.setData(userList.get(0));
            return result;
        } else if (userList.size() == 0) {
            result.setMessage("用户不存在或密码错误！");
            return result;
        }
        result.setMessage("登录失败！");
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT, readOnly = false)
    public Result set(OneParameter oneParameter) {
        Result result = new Result();
        if (oneParameter.getOwnerid() == null || oneParameter.getOwnerid() == 0 || oneParameter.getBookid() == null || oneParameter.getBookid() == 0) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        List<Bookstore> bookstoreList = bookstoreRepository.findAllByBookidAndOwneridAndStatus(oneParameter.getBookid().intValue(), oneParameter.getOwnerid().intValue(), 1);
        if (bookstoreList.size() > 0) {
            bookstoreList.forEach(e -> {
                if (oneParameter.getDeposit() != null) e.setDeposit(oneParameter.getDeposit());
                if (oneParameter.getDays() != null) e.setDays(oneParameter.getDays());
                if (oneParameter.getFee() != null) e.setFee(oneParameter.getFee());
                e.setWeigth(oneParameter.getWeigth());
                bookstoreRepository.save(e);
            });
            result.setStatus(1);
        }else {
            result.setMessage("未找到这本书,或这本书不是自有状态，不可变更属性！");
        }
        return result;
    }

    @Override
    public Result weixinLogin(OneParameter oneParameter) {
        Result result = new Result();
        if (StringUtils.isBlank(oneParameter.getOpenid())) {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        List<User> userList = userRepository.findByOpenid(oneParameter.getOpenid());
        if (userList.size() == 1) {
            result.setStatus(1);
            result.setData(userList.get(0));
            return result;
        } else if (userList.size() == 0) {
            result.setMessage("用户不存在！");
            return result;
        }
        result.setMessage("登录失败！");
        return result;
    }
}
