package org.blog;

import java.util.List;
import java.util.Objects;

public class Hobby {

  private String name;
  private List<String> tags;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<String> getTags() {
    return tags;
  }

  public void setTags(List<String> tags) {
    this.tags = tags;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Hobby hobby = (Hobby) o;
    return Objects.equals(name, hobby.name) && Objects.equals(tags, hobby.tags);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, tags);
  }

  @Override
  public String toString() {
    return "Hobby{" +
      "name='" + name + '\'' +
      ", tags=" + tags +
      '}';
  }
}
