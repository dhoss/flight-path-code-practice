package in.stonecolddev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {


  public record Flight(String start, String end) {
  }

  static void main() throws Exception {

    // A->E->C->D->B
    List<Flight> firstLegs = new ArrayList<>(
        List.of(
            new Flight("E", "C"),
            new Flight("D", "B"),
            new Flight("A", "E"),
            new Flight("C", "D")
        )
    );

    // D->A->E->B->C
    List<Flight> secondLegs = new ArrayList<>(
        List.of(
            new Flight("B", "C"),
            new Flight("E", "B"),
            new Flight("A", "E"),
            new Flight("D", "A")
        )
    );
    System.out.println("First trip:");
    System.out.println("Start and end destinations:");
    printPath(firstLegs, false);
    System.out.println("Full flight path:");
    printPath(firstLegs, true);

    System.out.println("Second trip:");
    System.out.println("Start and end destinations:");
    printPath(secondLegs, false);
    System.out.println("Full flight path:");
    printPath(secondLegs, true);
  }

  public static void printPath(List<Flight> legs, boolean displayFullPath) throws Exception {
    Map<String, String> path = new HashMap<>();
    legs.forEach(l -> path.put(l.start, l.end));

    List<String> fullPath = new ArrayList<>();

    String start = find(
        e -> !path.containsValue(e.getKey()),
        Map.Entry::getKey,
        path,
        new RuntimeException("no path root somehow"));
    String end = find(
        e -> !path.containsKey(e.getValue()),
        Map.Entry::getValue,
        path,
        new RuntimeException("no path end somehow"));

    fullPath.add(start);

    String nextKey = "";
    for (Map.Entry<String, String> entry : path.entrySet()) {
      boolean atRoot = false;

      if (nextKey.equalsIgnoreCase(""))
        nextKey = start;

      if (entry.getKey().equalsIgnoreCase(start)) {
        atRoot = true;
      }

      if (atRoot) {
        fullPath.add(entry.getValue());
        nextKey = entry.getValue();
      } else {
        String currentDestination = path.get(nextKey);
        fullPath.add(nextKey);
        fullPath.add(currentDestination);
        nextKey = path.get(currentDestination);
        if (nextKey == null)
          break;
      }
    }

    fullPath.add(end);
    if (!displayFullPath) {
      System.out.println(start + "->" + end);
    } else {
      System.out.println(String.join("->", fullPath.stream().distinct().toList()));
    }
  }

  private static String find(
      Predicate<Map.Entry<String, String>> filter,
      Function<Map.Entry<String, String>, String> mapper,
      Map<String, String> path, Exception exception) throws Exception {

    return path.entrySet()
        .stream()
        .filter(filter)
        .map(mapper)
        .findFirst()
        .orElseThrow(() -> exception);
  }
}