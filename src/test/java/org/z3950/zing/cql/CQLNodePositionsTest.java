package org.z3950.zing.cql;

import java.io.IOException;

public class CQLNodePositionsTest {

    // Set to true to use the query to infer positions of various attributes of the
    // CQLNode classes. Those start/stop offsets are not stored and need to be
    // computed (sometimes requiring the original query string to get exact
    // positions).
    public static final boolean INFER_OTHER_POSITIONS = false;

    public static void main(String[] args) throws CQLParseException, IOException {

        CQLParser parser = new CQLParser();

        String[] cqls = new String[] {
                // "cat",
                // " cat ",
                // "\"cat\"",
                // " \"cat\" ",
                // "\"cat\" or apple",
                "field any \"cat\"",
                // "field = \"cat\"",
                // "field <> cat",

                // "a lot of words",

                // ---

                // test terms
                // " field = \"val\" ",
                // " field = \"\\\"\" ",
                // " field = \"\\\"\\\"\" ",
                // " field = \"\\\"\\\"\\\"\" ",
                // " field = \"\\\"\\\"\\\"\\\"\" ",
                // " field = \"\\\" \\\"\" ",

                // test braces
                // "a = b and (field = \"value\")",
                // "a = b and ( field = \"value\" ) ",

                // this will fails with the parser
                // "(a) or (b)",
                // "(field = val) and (field = val)",
                // "(a)",
                // "(a = aa) or b = bb",
                // "(a = aa and d = ef) or b = bb",

                // test spaces
                // "dc.title any fish prox / unit=word dc.title any squirrel",
                // "dc.title any fish sortBy dc.date / sort.ascending",

                // test CQLTermNode parsing
                // "dc.TitlE any fish",
                // "dc.TitlE ANY fish",
                // "dc.TitlE Any fish",
                // "dc.TitlE aNy fish",
                // "dc.TitlE other fish",
                // "dc.TitlE Other fish",

                // ---

                // "dc.title any fish",
                // "dc.title any fish or dc.creator any sanderson",
                "dc.title any fish sortBy dc.date/sort.ascending",
                // "> dc = \"info:srw/context-sets/1/dc-v1.1\" dc.title any fish",

                // "fish",
                // "cql.serverChoice = fish", // this will not really work due to using defaults

                // "\"fish\"",
                // "fish",
                // "\"squirrels fish\"",
                // "\"\"",

                // "title any fish",
                // "dc.title any fish",

                // "dc.title any fish",
                // "dc.title cql.any fish",

                // "dc.title any/relevant fish",
                // "dc.title any/ relevant /cql.string fish",
                // "dc.title any/rel.algorithm=cori fish",

                // "dc.title any fish or (dc.creator any sanderson and dc.identifier =
                // \"id:1234567\")",

                // "dc.title any fish or/rel.combine=sum dc.creator any sanderson",
                // "dc.title any fish prox/unit=word/distance>3 dc.title any squirrel",

                // "\"cat\" sortBy dc.title",
                // "\"dinosaur\" sortBy dc.date/sort.descending dc.title/sort.ascending",

                // "> dc = \"http://deepcustard.org/\" dc.custardDepth > 10",
                // "> \"http://deepcustard.org/\" custardDepth > 10",

                // "dC.tiTlE any fish",
                // NOTE: fails to parse
                // "dc.TitlE Any/rEl.algOriThm=cori fish soRtbY Dc.TitlE",

        };

        for (String cql : cqls) {
            CQLNode node = null;
            try {
                node = parser.parse(cql);
            } catch (NullPointerException e) {
                System.err.println("Error parsing query '" + cql + "': " + e.getMessage());
                System.out.println();
                continue;
            }

            // System.out.println("CQL: " + cql);
            // System.out.println("Node: " + node.toCQL());
            // System.out.println("XCQL: " + node.toXCQL());

            // System.out.println();
            dumpTreeSubstring(node, 0, cql);
            System.out.println();
            // System.out.println("-".repeat(40));
            // System.out.println();
        }
    }

    // ---

    public static void printStartStopSubstring(int start, int stop, String cql) {
        System.out.print("|");
        if (start != -1 && stop != -1) {
            System.out.print(".".repeat(start));
            System.out.print(cql.substring(start, stop));
            System.out.print(".".repeat(cql.length() - stop));
        } else {
            System.out.print("~".repeat(cql.length()));
        }
        System.out.print("|");
    }

    public static void printStartStopSubstringCustom(String label, int level, int start, int stop, String cql) {
        printStartStopSubstring(start, stop, cql);
        System.out.print(" ");
        System.out.print(" ".repeat(level));
        System.out.print(label);
        System.out.println();
    }

    public static void printStartStopSubstringCustomWithSpaces(String label, int level, int start, int stop,
            String cql) {

        // try to strip/trim whitespaces
        if (start != -1 && stop != -1) {
            String content = cql.substring(start, stop);
            start = start + (content.length() - content.stripLeading().length());
            stop = stop - (content.length() - content.stripTrailing().length());
        }
        printStartStopSubstringCustom(label, level, start, stop, cql);
    }

    public static void dumpTreeSubstring(Modifier node, int level, String cql) {
        printStartStopSubstring(node.getStart(), node.getStop(), cql);
        System.out.print(" ");
        System.out.print(" ".repeat(level));
        System.out.print(node.getClass().getSimpleName());
        System.out.print(" → ");
        System.out.print(node.toCQL());
        System.out.println();

        if (INFER_OTHER_POSITIONS) {
            int typeStart = cql.toLowerCase().indexOf(node.getType(), node.getStart());
            int typeStop = typeStart + node.getType().length();
            printStartStopSubstringCustomWithSpaces("type", level + 1, typeStart, typeStop, cql);

            if (node.getComparison() != null) {
                int compStart = cql.indexOf(node.getComparison(), typeStop);
                int compStop = compStart + node.getComparison().length();
                printStartStopSubstringCustomWithSpaces("comparison", level + 1, compStart, compStop, cql);

                int valueStop = node.getStop();
                int valueStart = valueStop - node.getValue().length();
                printStartStopSubstringCustomWithSpaces("value", level + 1, valueStart, valueStop, cql);
            }
        }
    }

    public static void dumpTreeSubstring(CQLNode node, int level, String cql) {
        if (level == 0) {
            printStartStopSubstring(0, cql.length(), cql);
            System.out.print(" ");
            System.out.print("<root>");
            System.out.println();

            dumpTreeSubstring(node, level + 1, cql);
            return;
        }

        printStartStopSubstring(node.getStart(), node.getStop(), cql);
        System.out.print(" ");
        System.out.print(" ".repeat(level));
        System.out.print(node.getClass().getSimpleName());
        System.out.print(" → ");
        System.out.print(node.toCQL());
        System.out.println();

        if (node instanceof CQLTermNode) {
            CQLTermNode node2 = (CQLTermNode) node;

            if (INFER_OTHER_POSITIONS) {
                String index = node2.getIndex();
                boolean hasCustomIndex = (index != null && !index.equalsIgnoreCase("srw.serverChoice")
                        && !index.equalsIgnoreCase("cql.serverChoice"));
                if (hasCustomIndex) {
                    int indexStart = cql.indexOf(index, node.getStart());
                    int indexStop = indexStart + index.length();
                    printStartStopSubstringCustom("index", level + 1, indexStart, indexStop, cql);

                    dumpTreeSubstring(node2.getRelation(), level + 1, cql);

                    String term = node2.getTerm();
                    int termStop = node.getStop();
                    int termStart = termStop - term.length();
                    // check for quotes
                    if (term.indexOf('"') != -1) {
                        term = term.replace("\"", "\\\"");
                    }
                    int pos = cql.lastIndexOf(term, termStop);
                    if (pos != -1 && pos < termStart) {
                        termStart = pos - 1;
                        termStop = termStart + term.length() + 2;
                    }
                    printStartStopSubstringCustom("term", level + 1, termStart, termStop, cql);
                }
            } else {
                dumpTreeSubstring(node2.getRelation(), level + 1, cql);
            }

        } else if (node instanceof CQLRelation) {
            CQLRelation node2 = (CQLRelation) node;

            if (node2.getModifiers().size() > 0) {
                for (Modifier modifier : node2.getModifiers()) {
                    dumpTreeSubstring(modifier, level + 1, cql);
                }
            }

        } else if (node instanceof CQLBooleanNode) {
            CQLBooleanNode node2 = (CQLBooleanNode) node;

            dumpTreeSubstring(node2.getLeftOperand(), level + 1, cql);

            if (INFER_OTHER_POSITIONS) {
                int opStart = node2.getLeftOperand().getStop();
                int opStop = node2.getRightOperand().getStart();

                printStartStopSubstringCustomWithSpaces("operator", level + 1, opStart, opStop, cql);

                if (node2.getModifiers().size() > 0) {
                    opStop = node2.getModifiers().get(0).getStart();
                    printStartStopSubstringCustomWithSpaces("operator", level + 2, opStart, opStop, cql);

                    for (Modifier modifier : node2.getModifiers()) {
                        dumpTreeSubstring(modifier, level + 2, cql);
                    }
                }
            }

            dumpTreeSubstring(node2.getRightOperand(), level + 1, cql);
        } else if (node instanceof CQLSortNode) {
            CQLSortNode node2 = (CQLSortNode) node;

            dumpTreeSubstring(node2.getSubtree(), level + 1, cql);

            int start = node2.getStart() + 6;
            for (ModifierSet ms : node2.getSortIndexes()) {

                if (INFER_OTHER_POSITIONS) {
                    int baseStart = cql.indexOf(ms.getBase(), start);
                    int baseStop = baseStart + ms.getBase().length();
                    printStartStopSubstringCustom("base", level + 1, baseStart, baseStop, cql);
                }

                if (ms.getModifiers().size() > 0) {
                    for (Modifier modifier : ms.getModifiers()) {
                        dumpTreeSubstring(modifier, level + 2, cql);
                    }
                }
            }

        } else if (node instanceof CQLPrefixNode) {
            CQLPrefixNode node2 = (CQLPrefixNode) node;

            if (INFER_OTHER_POSITIONS) {
                int skip = node2.getStart();
                if (node2.getPrefix().getName() != null) {
                    int nameStart = cql.indexOf(node2.getPrefix().getName(), node2.getStart());
                    int nameStop = nameStart + node2.getPrefix().getName().length();
                    skip = nameStop + 1;
                    printStartStopSubstringCustom("name", level + 1, nameStart, nameStop, cql);
                }

                int identStart = cql.indexOf(node2.getPrefix().getIdentifier(), skip);
                int identStop = identStart + node2.getPrefix().getIdentifier().length();
                printStartStopSubstringCustom("identifier", level + 1, identStart, identStop, cql);
            }

            dumpTreeSubstring(node2.getSubtree(), level + 1, cql);
        }
    }

}
