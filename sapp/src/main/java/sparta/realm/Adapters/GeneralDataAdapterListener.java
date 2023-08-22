package sparta.realm.Adapters;

public interface GeneralDataAdapterListener<RM> {
    void onClick(RM item);
    default void onNewDataRequested() {

    }

}
