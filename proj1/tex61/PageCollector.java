package tex61;

import java.util.List;

/** A PageAssembler that collects its lines into a designated List.
 *  @author Charles Lee
 */
class PageCollector extends PageAssembler {

    /** A new PageCollector that stores lines in OUT. */
    PageCollector(List<String> out) {
        _lines = out;
    }

    /** Add LINE to my List. */
    @Override
    void write(String line) {
        _lines.add(line);
    }
    /** List of strings _lines. */
    private List<String> _lines;
    /** Returns  List<String> _LINES. */
    List<String> getLines() {
        return _lines;
    }
}
