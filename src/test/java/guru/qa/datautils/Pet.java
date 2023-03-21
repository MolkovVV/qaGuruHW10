package guru.qa.datautils;

import java.util.List;

public class Pet {
    public Integer id;
    public Category category;
    public String name;
    public List<String> photoUrls;
    public List<Tag> tags;
    public String status;

    public static class Category {
        public Integer id;
        public String name;
    }

    public static class Tag {
        public Integer id;
        public String name;
    }
}
