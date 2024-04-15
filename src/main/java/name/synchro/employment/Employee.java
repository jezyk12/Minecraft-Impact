package name.synchro.employment;

public interface Employee {
    String EMPLOYER = "employer";
    String TYPE_BLOCK = "block_entity";
    String TYPE_ENTITY = "mob_entity";

    WorkingHandler getWorkingHandler();
}
