package de.uwuwhatsthis.supportBotForDev.objects;

public class ServerTimer implements Runnable {

    private final ShowcaseServer server;
    private boolean run = true;
    private boolean exit = false;
    private int secondsToSleep = 5*60 + 1;

    public ServerTimer(ShowcaseServer server){
        this.server = server;
    }

    @Override
    public void run() {
        while (true){

            // when the thread should exit
            if (exit) break;

            // when the thread shouldn't update the timers, but should not exit
            if (!run){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                continue;
            }

            // 5 minute timer to wait so we don't hit the discord name channel limit
            if (secondsToSleep > 0){

                if (exit) break;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                secondsToSleep --;
                continue;
            }

            secondsToSleep = 5*60 + 1;


            server.updateTimerChannels();
        }
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public void setExit(boolean exit) {
        this.exit = exit;
    }
}
