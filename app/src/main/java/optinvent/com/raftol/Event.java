package optinvent.com.raftol;

abstract class Event {
    private long timeToRun;
    private boolean toRun;

    Event(long _timeToRun) {
        timeToRun = _timeToRun;
        toRun = true;
    }

    void tryToRun(Core core) {
        if (toRun && (System.currentTimeMillis() >= timeToRun)) {
            toRun = false;
            run(core);
        }
    }

    abstract void run(Core core);
}
