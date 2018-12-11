package eg.edu.alexu.csd.oop.jdbc.cs50;

import java.sql.SQLTimeoutException;

public class TimeThread extends Thread implements Runnable {
    private boolean flag = false;
    private int time;

    public void setTime(int t) {
        time = t;
    }

    public boolean timeIsOver() {
        return flag;
    }

    public TimeThread() {
        // TODO Auto-generated constructor stub
        System.out.println("process starts");
    }

    public void run() {
        try {
            Thread.sleep(time * 1000);
            flag = true;
            throw new SQLTimeoutException("Exceeding Time Limit!");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLTimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}