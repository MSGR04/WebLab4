package web.lab.web41.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import web.lab.web41.entity.Point;
import web.lab.web41.entity.User;
import web.lab.web41.repository.Repository_of_Points;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final Repository_of_Points pointRepository;
    private final UserService userService;

    public boolean isValidPoint(Double x, Double y, Double r) {
        return !(x < -3 || x > 3 || y < -3 || y > 3 || r < 1 || r > 3);
    }

    public Point processPoint(Double x, Double y, Double r, String username) {
        User user = userService.findByUsername(username).orElseThrow();
        boolean hit = isPointInArea(x, y, r);

        Point point = new Point();
        point.setX(x);
        point.setY(y);
        point.setR(r);
        point.setHit(hit);
        point.setUser(user);

        return pointRepository.save(point);
    }

    private boolean isPointInArea(double x, double y, double r) {
        return (x >= 0 && y <= 0 && y >= x / 2 - r / 2) ||
               (x <= 0 && y <= 0 && x >= -r && y >= -r / 2 ) ||
               (x >= 0 && y >= 0 && x * x + y * y <= (r * r));
    }

    public List<Point> getPointsByUsername(String username) {
        return pointRepository.findAllByUser(
                userService.findByUsername(username).orElseThrow()
        );
    }
}