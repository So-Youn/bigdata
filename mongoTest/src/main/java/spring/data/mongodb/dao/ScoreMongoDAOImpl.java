package spring.data.mongodb.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import spring.data.mongodb.dto.ScoreDTO;
//spring-data에서 지원되는 Repository와 MongoTemplate클래스를 이용해서 mongodb에 액세스 하는 기능을 구현
import spring.data.mongodb.dto.ScoreRepository;
@Repository
public class ScoreMongoDAOImpl implements ScoreMongoDAO {
	// 페이징 처리를 편하게 하기위해
	//CLRUD를 위한 기본 기능도 제공 (SPRING-DATA의 COMMON 라이브러리에서 지원하는 기능)
	@Autowired
	ScoreRepository scoreRepository;
	//mongodb를 연동하기 위한 기능을 제공
	//(spring-data-mongodb 라이브러리에서 지원하는 기능)
	@Autowired
	MongoTemplate mongoTemplate;
		
	@Override
	public List<ScoreDTO> findCriteria(String key, String value) {
		String[] data = key.split(",");
		System.out.println("조회된 key:"+key);
		
		//query클래스의 addCriteria 메소드와 Criteria 클래스의 where 메소드의 활용
		//query.addCriteria(Criteria.where(data[0]).is(value)); //Class.where - 정적 메소드
		
		Criteria criteria = new Criteria();
		criteria.andOperator(Criteria.where(data[0]).is(value),
				Criteria.where("addr").is("인천")); // $and      
		//(Criteria... : 확장이 가능하다는 의미
		Query query = new Query(criteria);
		List<ScoreDTO> mongolist = mongoTemplate.find(query, ScoreDTO.class, "score");
		return mongolist; 
	}
	
	
	
	
	//mongodb가 json으로 모든 작업을 처리하므로  key,value로 조건을 정의
	//spring-data-mongodb에서 이러한 조건을 처리하는 객체를 만들어서 제공
	//Criteria - 조건(정확하게 일치)
	@Override
	public ScoreDTO findById(String key, String value) {
		//1. 조건을 객체로 작성
		Criteria criteria = new Criteria(key);//조건 처리 제공해주는 클래스 Criteria
		//2. 조건에 대한 설정 - value 셋팅
		criteria.is(value);
		//3. Criteria를 이용해서 Query객체를 작성
		// - mongodb 내부에서 인식할 조건을 정의하는 객체
		Query query = new Query(criteria);
		//4. MongoTemplate 클래스의 메소드를 호출하며 Query객체를 이용해서 매개변수로 전달
		ScoreDTO document = mongoTemplate.findOne(query, ScoreDTO.class, "score");
		return document;
	}

	@Override
	public void insertDocument(ScoreDTO doc) {
		mongoTemplate.insert(doc); // 기본 제공 메소드
		

	}

	@Override
	public void insertAllDocument(List<ScoreDTO> docs) {
		mongoTemplate.insertAll(docs); //한꺼번에 insert
	}

	@Override
	public void update(ScoreDTO document) {
		//수정할 document에 조건을 적용 - RDBMS의 where절
		Criteria criteria = new Criteria("id");
		criteria.is(document.getId());
		Query query = new Query(criteria);
		//수정될 데이터를 셋팅 - RDBMS의 SET절
		//update객체에 수정할 데이터 셋팅
		Update update = new Update();
		update.set("addr", document.getAddr());
		update.set("dept", document.getDept());
		
		//mongoTemplate의 수정기능 호출
		mongoTemplate.updateMulti(query, update, "score");
	}

	@Override
	public List<ScoreDTO> findAll() {
		List<ScoreDTO> mongolist = (List<ScoreDTO>)scoreRepository.findAll(); 
		System.out.println(mongolist.size());
		return mongolist;
	}

	@Override
	public List<ScoreDTO> findAll(int pageNo) {
		//PagingAndSortingRepository에서 제공하는 findAll을 호출하면 Spring-data내부에서 
		//페이징처리가 된 객체를 전달받을 수 있다.
		//페이징 처리를 내부에서 할 수 있도록 필요한 정보를 findAll메소드에서 pageable타입의 매개변수로 전달받는다.
		//[정보전달]
		//pageable을 구현하고 있는 pagerequest객체를 넘겨준다.
		//                   ----------- 현재 페이지 번호(page),size(화면에 표시할 게시글 갯수)
		//[리턴]
		//findAll이 처리되면 페이징된 객체를 page타입으로 리턴한다.
		//pageRequest에 전달한 사이즈와 page번호를 기준으로 조회한 데이터를 매핑해서 넘겨준다.
		Page<ScoreDTO> pageList= scoreRepository.findAll(new PageRequest(pageNo,10));//5개를 하나의 화면에서 자른다 (contentNo)
		//getContent()는
		//페이지 객체가 갖고 있는 페이징 된 리스트 객체를 반환
		List<ScoreDTO> mongolist = pageList.getContent();
		
		return mongolist;
	}

}
