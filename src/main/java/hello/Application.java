package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@SpringBootApplication
@RestController
public class Application {

    static class Self {
        public String href;
    }

    static class Links {
        public Self self;
    }

    static class PlayerState {
        public Integer x;
        public Integer y;
        public String direction;
        public Boolean wasHit;
        public Integer score;
    }

    static class Arena {
        public List<Integer> dims;
        public Map<String, PlayerState> state;
    }

    static class ArenaUpdate {
        public Links _links;
        public Arena arena;
    }

    static class Point {
        public int x;
        public int y;
    }


    public static final String COMMAND_F = "F";
    public static final String COMMAND_R = "R";
    public static final String COMMAND_L = "L";
    public static final String COMMAND_T = "T";


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @GetMapping("/")
    public String index() {
        return "Let the battle begin!";
    }

    private static String generatePlayerLocationKey(PlayerState playerState) {
        return playerState.x + "," + playerState.y;
    }

    private static String generatePalyerLocationKey(Point point) {
        return point.x + "," + point.y;
    }

    @PostMapping("/**")
    public String index(@RequestBody ArenaUpdate arenaUpdate) {
        System.out.println(arenaUpdate);
        int i = new Random().nextInt(4);


        String selfId = arenaUpdate._links.self.href;

        int w = arenaUpdate.arena.dims.get(0);
        int h = arenaUpdate.arena.dims.get(1);

        PlayerState myState = arenaUpdate.arena.state.get(selfId);
        Map<String, PlayerState> xy2Player = new HashMap<>();
        arenaUpdate.arena.state.forEach((k, v) -> {
            String key = generatePlayerLocationKey(v);
            xy2Player.put(v.x + "," + v.y, v);
        });

        //escape
//        if (myState.wasHit) {
//            System.out.println("escape");
//            if (myState.direction.equals("N")) {
//                return COMMAND_L;
//            } else if (myState.direction.equals("S")) {
//                return COMMAND_R;
//            } else if (myState.direction.equals("E")) {
//                return COMMAND_L;
//            } else if (myState.direction.equals("W")) {
//                return COMMAND_R;
//            }
//        }

        Optional<String> fire = fireAndGainPoint(myState, xy2Player);
        if (fire.isPresent()) {
            return fire.get();
        }

        System.out.println("Need to move ....");
        //random move

        if (i == 0) {
            return COMMAND_F;
        } else if (i == 1) {
            return COMMAND_L;
        } else if (i == 2) {
            return COMMAND_R;
        } else if (i == 3) {
            return COMMAND_T;
        }
        return COMMAND_F;


    }


    private Optional<String> fireAndGainPoint(PlayerState myself, Map<String, PlayerState> xy2Player) {

        String key = generatePalyerLocationKey(getPoint(myself, 1));

        if (xy2Player.containsKey(key)) {
            System.out.println("hit key in one step");
            return Optional.of(COMMAND_F);
        }

        key = generatePalyerLocationKey(getPoint(myself, 2));

        if (xy2Player.containsKey(key)) {
            System.out.println("hit key in two steps");
            return Optional.of(COMMAND_F);
        }

        key = generatePalyerLocationKey(getPoint(myself, 3));
        if (xy2Player.containsKey(key)) {
            System.out.println("hit key in three steps");
            return Optional.of(COMMAND_F);
        }


        return Optional.empty();
    }

    public static Point getPoint(PlayerState myself, int step) {
        int x = myself.x;
        int y = myself.y;
        switch (myself.direction) {
            case "N":
                y--;
                break;
            case "S":
                y++;
                break;
            case "E":
                x++;
                break;
            case "W":
                x--;
                break;
        }

        Point point = new Point();
        point.x = x;
        point.y = y;
        return point;
    }


}

