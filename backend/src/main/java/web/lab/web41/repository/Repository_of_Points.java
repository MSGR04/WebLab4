package web.lab.web41.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import web.lab.web41.entity.Point;
import web.lab.web41.entity.User;

import java.util.List;

@Repository
public interface Repository_of_Points extends JpaRepository<Point, Long> {
    List<Point> findAllByUser(User user);
}
