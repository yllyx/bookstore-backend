package com.myweb.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.myweb.dao.jpa.hibernate.BookRepository;
import com.myweb.dao.jpa.hibernate.BookstoreRepository;
import com.myweb.dao.jpa.hibernate.RecordRepository;
import com.myweb.dao.jpa.hibernate.UserRepository;
import com.myweb.pojo.Book;
import com.myweb.pojo.Bookstore;
import com.myweb.pojo.Record;
import com.myweb.service.TwoService;
import com.myweb.vo.TwoParameter;
import com.utils.CommonConst;
import com.utils.Result;

@Service("TwoService")
@SuppressWarnings("All")
@Transactional(readOnly = true)
public class TwoServiceImpl implements TwoService
{

    private static final Logger logger = LogManager.getLogger(TwoServiceImpl.class);

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
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result borrowRequest(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 1)
        {
            result.setMessage("未找到这本书,或这本书不可借！");
            return result;
        }
        Record record = new Record();
        record.setUser(userRepository.findOne(twoParameter.getUserid()));
        if (StringUtils.isNotBlank(twoParameter.getLetter()))
            record.setLetter(twoParameter.getLetter());
        record.setStatus(1);
        record.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        record.setBookstore(bookstore);
        recordRepository.save(record);
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnRequest(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getUser().getId() != twoParameter.getUserid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }
        bookstore.getRecord().forEach(e -> {
            if ((e.getStatus() == 7) && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(8);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        });
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result borrowAgree(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 1
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可借！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if (e.getStatus() == 1 && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(2);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result borrowDisagree(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 1
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可借！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if (e.getStatus() == 1 && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(3);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnDisagree(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getUser().getId() != twoParameter.getUserid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 10) && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(11);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result borrowConfirm(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 1
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可借！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 6 || e.getStatus() == 4)
                && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(7);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
                bookstore.setUser(userRepository.findOne(twoParameter.getUserid()));
                bookstoreRepository.save(bookstore);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnAgree(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }

        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 10) && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(12);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
                bookstore.setUser(null);
                bookstoreRepository.save(bookstore);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnFee(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 9 || e.getStatus() == 11)
                && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(10);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnConfirm(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 8) && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(9);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result returnOk(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 2
            || bookstore.getOwner().getId() != twoParameter.getOwnerid())
        {
            result.setMessage("未找到这本书,或这本书不可还！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 9 || e.getStatus() == 11)
                && e.getUser().getId() == twoParameter.getUserid())
            {
                e.setStatus(12);
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    public Result borrowMyrequest(TwoParameter twoParameter)
    {
        Result result = new Result();
        List<Record> recordList = new ArrayList<>();
        if (twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        if (twoParameter.getStatus() == null || twoParameter.getStatus() == 0)
        {
            recordList = recordRepository.findAllByUserid(twoParameter.getUserid());
        }
        else
        {
            recordList = recordRepository.findAllByUseridAndStatus(twoParameter.getUserid(),
                twoParameter.getStatus());
        }
        for (int i = 0; i < recordList.size(); i++)
        {
            recordList.get(i).getBookstore().setRecord(null);
        }
        result.setStatus(1);
        result.setData(recordList);
        return result;
    }

    @Override
    public Result borrowTorequest(TwoParameter twoParameter)
    {
        Result result = new Result();
        List<Record> recordList = null;// new ArrayList<>();
        if (twoParameter.getOwnerid() == null || twoParameter.getOwnerid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        if (twoParameter.getStatus() == null || twoParameter.getStatus() == 0)
        {
            recordList = recordRepository.findAllByOwnerid(twoParameter.getOwnerid());
        }
        else
        {
            recordList = recordRepository.findAllByOwneridAndStatus(twoParameter.getOwnerid(),
                twoParameter.getStatus());
        }
        for (int i = 0; i < recordList.size(); i++)
        {
            recordList.get(i).getBookstore().setRecord(null);
        }
        result.setStatus(1);
        result.setData(recordList);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.DEFAULT,
        readOnly = false)
    public Result borrowStart(TwoParameter twoParameter)
    {
        Result result = new Result();
        if (twoParameter.getBookstoreid() == null || twoParameter.getBookstoreid() == 0
            || twoParameter.getUserid() == null || twoParameter.getUserid() == 0)
        {
            result.setMessage("必须的参数不能为空!");
            return result;
        }
        Bookstore bookstore = bookstoreRepository.findOne(twoParameter.getBookstoreid());
        if (bookstore == null || bookstore.getStatus() != 1)
        {
            result.setMessage("未找到这本书,或这本书不可借！");
            return result;
        }
        List<Record> recordList = bookstore.getRecord();
        for (int i = 0; i < recordList.size(); i++)
        {
            Record e = recordList.get(i);
            if ((e.getStatus() == 2) && e.getUser().getId() == twoParameter.getUserid())
            {
                if (bookstore.getDeposit() == null || bookstore.getDeposit().intValue() == 0)
                {
                    e.setStatus(4);

                }
                else
                {
                    e.setStatus(5);
                }
                e.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                recordRepository.save(e);
            }
        }
        for (int t = 0; t < bookstore.getRecord().size(); t++)
        {
            bookstore.getRecord().get(t).setBookstore(null);
        }
        result.setStatus(1);
        result.setData(bookstore);
        return result;
    }

    @Override
    public Result bookUploadNew(TwoParameter twoParameter)
    {
        if (twoParameter.getPage() == null)
        {
            twoParameter.setPage(0);
        }
        Result result = new Result();
        try
        {
            Sort sort = new Sort(Direction.DESC, "id");
            Pageable pageable =
                new PageRequest(twoParameter.getPage(), CommonConst.PAGE_SIZE, sort);
            Page<Book> books = bookRepository.findAll(pageable);
            result.setStatus(CommonConst.SUCCESS);
            Map<Object, Object> data = new HashMap<Object, Object>();
            data.put("books", books.getContent());
            data.put("next", twoParameter.getPage() + 1);
            result.setData(data);
            result.setMessage("获取第" + twoParameter.getPage() + "页最新书籍成功");
        }
        catch (Exception e)
        {
            result.setStatus(CommonConst.FAIL);
            result.setMessage("获取最新书籍出错：" + e.getMessage());
        }

        return result;
    }
}
