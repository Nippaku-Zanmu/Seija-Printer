package com.kijinseija.seija_printer.print_main.printer.task_manager;

public class Task {
    public Runnable task;

    public Task(Runnable task) {
        this.task = task;
    }
    public Runnable getTask(){
        return task;
    }
}
