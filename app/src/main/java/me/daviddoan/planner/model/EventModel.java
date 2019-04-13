package me.daviddoan.planner.model;

import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class EventModel {
    private static EventModel firstInstance = null;
    private ArrayList<EventImpl> eventList = new ArrayList<>();
    private ArrayList<MovieImpl> movieList = new ArrayList<>();

    private EventModel(){

    }

    public static EventModel getInstance(){
        if(firstInstance == null){

            firstInstance = new EventModel();

        }
        return firstInstance;
    }

    public MutableLiveData<ArrayList<EventImpl>> getEventList() {
        setEventList();

        MutableLiveData<ArrayList<EventImpl>> data = new MutableLiveData<>();
        data.setValue(eventList);

        return data;
    }

    public MutableLiveData<ArrayList<MovieImpl>> getMovieList() {
        setMovieList();

        MutableLiveData<ArrayList<MovieImpl>> data = new MutableLiveData<>();
        data.setValue(movieList);

        return data;
    }

    private void setEventList() {
        eventList.add(new EventImpl
                ("1", "Freaky Friday", "2/01/2019 1:00:00 AM",
                        "2/01/2019 3:00:00 AM", "RMIT Capitol Theatre",
                        "-37.814795, 144.966119", null));
        eventList.add(new EventImpl("2", "Scary Saturday", "3/01/2019 2:00:00 AM",
                "3/01/2019 4:00:00 AM", "HOYTS The District Docklands",
                "-37.811363, 144.936967", null));
    }

    private void setMovieList() {
        movieList.add(new MovieImpl("1", "Blade Runner", "1982", "BladeRunner1982.jpg"));
        movieList.add(new MovieImpl("2", "Hackers", "1995", "Hackers.jpg"));
    }

}
