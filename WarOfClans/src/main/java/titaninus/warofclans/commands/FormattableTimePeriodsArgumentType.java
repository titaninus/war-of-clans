package titaninus.warofclans.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.text.Text;
import titaninus.warofclans.core.FormattableTime;
import titaninus.warofclans.core.FormattableTimePeriods;

import java.util.Arrays;
import java.util.Collection;

public class FormattableTimePeriodsArgumentType implements ArgumentType<FormattableTimePeriods> {
    private static final Collection<String> EXAMPLES = Arrays.asList("10d|10m|10h", "1d1m1h1s", "1d1m1h1s|1d1m1h1s", "1d1");

    public static FormattableTimePeriods getTime(final CommandContext<?> context, final String name) {
        return context.getArgument(name, FormattableTimePeriods.class);
    }

    public static FormattableTimePeriodsArgumentType create() {
        return new FormattableTimePeriodsArgumentType();
    }

    @Override
    public FormattableTimePeriods parse(final StringReader reader) throws CommandSyntaxException {
        int argBeginning = reader.getCursor(); // The starting position of the cursor is at the beginning of the argument.
        if (!reader.canRead()) {
            reader.skip();
        }

        // Now we check the contents of the argument till either we hit the end of the command line (When canRead becomes false)
        // Otherwise we go till reach reach a space, which signifies the next argument
        while (reader.canRead() && reader.peek() != ' ') { // peek provides the character at the current cursor position.
            reader.skip(); // Tells the StringReader to move it's cursor to the next position.
        }

        // Now we substring the specific part we want to see using the starting cursor position and the ends where the next argument starts.
        final String result = reader.getString().substring(argBeginning, reader.getCursor());
        var periods = FormattableTimePeriods.TryParse(result);
        if (periods == null) {

            throw new SimpleCommandExceptionType(Text.literal("Cannot parse formattable time periods")).createWithContext(reader);
        }
        return periods;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof final FormattableTimePeriodsArgumentType that)) return false;

        return true;
    }
    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}