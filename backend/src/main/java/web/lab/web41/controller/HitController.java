package web.lab.web41.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import web.lab.web41.entity.Point;
import web.lab.web41.service.PointService;
import web.lab.web41.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HitController {

    private final PointService pointService;
    private final UserService userService;

    @GetMapping("/check")
    public ResponseEntity<?> checkPoint(
            @RequestParam Double x,
            @RequestParam Double y,
            @RequestParam Double r,
            @RequestParam String username,
            @RequestParam String password) {

        if (!userService.validateUser(username, password)) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        if (!pointService.isValidPoint(x, y, r)) {
            return ResponseEntity.status(400).body("Invalid point parameters");
        }

        Point point = pointService.processPoint(x, y, r, username);
        return ResponseEntity.ok(point);
    }

    @GetMapping("/getPoints")
    public ResponseEntity<?> getPoints(
            @RequestParam String username,
            @RequestParam String password) {
        if (!userService.validateUser(username, password)) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }
        List<Point> points = pointService.getPointsByUsername(username);
        return ResponseEntity.ok(points);
    }
}