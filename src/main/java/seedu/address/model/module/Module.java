package seedu.address.model.module;


public class Module {
    private final String name;
    private final String description;

    public Module(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    public String getProfessor() {
        return "Prof Jay";
    }
}

