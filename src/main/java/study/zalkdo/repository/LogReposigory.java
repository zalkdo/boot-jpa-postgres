package study.zalkdo.repository;

import study.zalkdo.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogReposigory extends JpaRepository<Log, String> {

}
