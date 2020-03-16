package io.alcatraz.audiohq.beans;

public interface AudioHQNativeInterface<T> {
    void onSuccess(T result);
    void onFailure(String reason);
}
