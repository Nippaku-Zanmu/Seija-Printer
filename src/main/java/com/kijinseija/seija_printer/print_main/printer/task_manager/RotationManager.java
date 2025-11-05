/*
 * Copyright 2025 Nippaku_Zanmu
 * SPDX-License-Identifier: gplv3
 */

package com.kijinseija.seija_printer.print_main.printer.task_manager;

import com.kijinseija.seija_printer.print_main.printer.util.records.RotationData;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.entity.player.SendMovementPacketsEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RotationManager {
    public static  final  RotationManager INSTANCE= new RotationManager();
    private RotationManager() {
        MeteorClient.EVENT_BUS.subscribe(this);
    }


    private final List<RotationData> taskList = new LinkedList();
    public int taskSize(){
        return taskList.size();
    }




    @EventHandler
    public void consume(TickEvent.Pre event) {
        if (!taskList.isEmpty()){
            RotationData d = taskList.getFirst();
            Rotations.rotate(d.yaw(),d.pitch(),0,d.task());

            taskList.removeFirst();
        }else if (!bufferedTasks.isEmpty()){
            for (Task t : bufferedTasks) {
                t.getTask().run();
            }
            bufferedTasks.clear();
        }

    }

    public boolean addTask(RotationData data) {
        if (taskList.size() > 10)
            return false;
        //todo smooth rotate 7/24
        taskList.add(data);
        return true;
    }

    private final static List<Task> bufferedTasks = new LinkedList<>();

    public boolean addTask(List<Task> tasks) {
        for (Task task : tasks) {
            if (task instanceof RotationTask rtask) {
                final List<Task> executes = new ArrayList<>(bufferedTasks);
                bufferedTasks.clear();
                boolean b = addTask(new RotationData(rtask.rdata.yaw(), rtask.rdata.pitch(), () -> {
                    for (Task execute : executes) {
                        if (execute.getTask() != null) execute.getTask().run();
                    }
                }));
                if (!b) return b;
            } else {
                bufferedTasks.add(task);
            }
        }
        return true;
    }

}
