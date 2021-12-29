package ecs.components;

import java.util.Date;

import ecs.Component;

public class Timer extends Component {
  

    public static Timer standard() {
        return new Timer();
    }
}
