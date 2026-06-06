package dev.threeadd.packeteventssk.element.general.expression;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.Skin;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprSkinFromValue extends SimpleExpression<Skin> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprSkinFromValue.class, Skin.class, "[new] [player] skin from [value] %string% [(with|and) signature %-string%]")
                .name("General - Player Skin From Base64/Signature")
                .description("""
                        Creates a skin object using a Base64 value and an optional cryptographic signature.
                        The value contains the texture URL, while the signature proves the skin is authentic from Mojang.
                        """)
                .examples("""
                        on load:
                            set {_base64} to "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0="
                            set {_signature} to "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="
                            set {-skin} to skin from value {_base64} with signature {_signature}
                        
                        command defaultSkin:
                            trigger:
                                set displayed skin of player to {-skin}
                        """)
                .since("1.0.0")
                .register();
    }

    private Expression<String> valueExpr;
    private @Nullable Expression<String> signatureExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.valueExpr = (Expression<String>) expressions[0];
        this.signatureExpr = (Expression<String>) expressions[1];

        return true;
    }

    @Override
    protected Skin @Nullable [] get(Event event) {
        String value = this.valueExpr.getSingle(event);
        if (value == null) return new Skin[0];

        String signature = this.signatureExpr != null ? this.signatureExpr.getSingle(event) : null;

        return new Skin[]{new Skin(value, signature)};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Skin> getReturnType() {
        return Skin.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String skinValue = this.valueExpr.getSingle(event);
        String signaturePart = this.signatureExpr != null ? " and signature " + this.signatureExpr.toString(event, debug) : "";

        return String.format("skin from value %s%s", skinValue, signaturePart);
    }
}