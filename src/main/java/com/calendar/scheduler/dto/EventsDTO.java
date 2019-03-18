package com.calendar.scheduler.dto;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.calendar.scheduler.model.EventObj;

public class EventsDTO implements IEventsMongoDB {
    @Autowired
    private IEventsMongoDB eventsMongo;

	@Override
	public <S extends EventObj> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EventObj> findAll() {
		// TODO Auto-generated method stub
		return this.eventsMongo.findAll();
	}

	@Override
	public List<EventObj> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> S insert(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> List<S> insert(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<EventObj> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> S save(S entity) {
		// TODO Auto-generated method stub
		return this.eventsMongo.save(entity);
	}

	@Override
	public Optional<EventObj> findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<EventObj> findAllById(Iterable<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(EventObj entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll(Iterable<? extends EventObj> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		this.eventsMongo.deleteAll();

	}

	@Override
	public <S extends EventObj> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends EventObj> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends EventObj> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public EventObj findByEventName(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EventObj findByStartDate(Date startDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
