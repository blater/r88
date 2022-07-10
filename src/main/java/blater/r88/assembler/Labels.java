package blater.r88.assembler;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

@Getter
public class Labels {
    private Map<String, Label> labels = new HashMap<>();

    public Label addLabel(Tokenizer tokenizer, int pc) {
        Label l = Label.parseLabelDeclaration(tokenizer).orElse(null);
        if (!labels.containsKey(l.getName())) {
            l.setValue(pc);
            labels.put(l.getName(), l);
        } else {
            l = labels.get(l.getName());
            l.setValue(pc);
        }
        return l;
    }

    public boolean isLabel(Token target) {
        return target != null && labels.containsKey(target.valueWithoutPrefix());
    }

    private Optional<Label> get(Token token2) {
        String target = token2 == null ? "" : token2.valueWithoutPrefix();
        if (labels.containsKey(target))
            return of(labels.get(target));
        else
            return empty();
    }

    public String tryToResolve(Token token2, int pc) {
        Optional<Label> label = get(token2);
        if (label.isPresent()) {
            if (label.get().getValue() == null) {
                // it is a forward reference that has been seen previously
                label.get().getNeedsResolving().add(pc - 1);
                return "";
            } else {
                // it is a backward reference
                return String.valueOf(label.get().getValue());
            }
        }
        // else it is an undefined label forward reference. cannot resolve.
        Label l = new Label(token2.getValue());
        l.getNeedsResolving().add(pc - 1);
        labels.put(l.getName(), l);
        return "";
    }
}
