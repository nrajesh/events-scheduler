package com.calendar.scheduler.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.calendar.scheduler.model.ScheduleObj;

public class ScheduleDTO implements IScheduleMongoDB {
	
    @Autowired
    private IScheduleMongoDB schedulerMongo;

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
	public void deleteAll() {
		this.schedulerMongo.deleteAll();

	}

	@Override
	public <S extends ScheduleObj> List<S> saveAll(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ScheduleObj> findAll() {
		return this.schedulerMongo.findAll();
	}

	@Override
	public List<ScheduleObj> findAll(Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> S insert(S entity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> List<S> insert(Iterable<S> entities) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> List<S> findAll(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> List<S> findAll(Example<S> example, Sort sort) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Page<ScheduleObj> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> S save(S entity) {
		// TODO Auto-generated method stub
		return this.schedulerMongo.insert(entity);
	}

	@Override
	public Optional<ScheduleObj> findById(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsById(Integer id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<ScheduleObj> findAllById(Iterable<Integer> ids) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete(ScheduleObj entity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAll(Iterable<? extends ScheduleObj> entities) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <S extends ScheduleObj> Optional<S> findOne(Example<S> example) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> Page<S> findAll(Example<S> example, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends ScheduleObj> long count(Example<S> example) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public <S extends ScheduleObj> boolean exists(Example<S> example) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ScheduleObj findByEventName(String eventName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScheduleObj findByOccurrenceDate(LocalDate occurrenceDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
