package org.telegram.ui.Components.Paint;

import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes5.dex */
public class ShaderSet {
    private static final String ATTRIBUTES = "attributes";
    private static final Map<String, Map<String, Object>> AVAILBALBE_SHADERS = createMap();
    private static final String FRAGMENT = "fragment";
    private static final String PAINT_BLITWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLITWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main (void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor.rgb *= gl_FragColor.a; }";
    private static final String PAINT_BLIT_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; varying vec2 varTexcoord; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; }";
    private static final String PAINT_BRUSHLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { vec4 f = texture2D(texture, varTexcoord.st, 0.0); gl_FragColor = vec4(f.r * varIntensity, f.g, f.b, 0.0); }";
    private static final String PAINT_BRUSH_FSH = "precision highp float; varying vec2 varTexcoord; varying float varIntensity; uniform sampler2D texture; void main (void) { gl_FragColor = vec4(1, 1, 1, varIntensity * texture2D(texture, varTexcoord.st, 0.0).r); }";
    private static final String PAINT_BRUSH_VSH = "precision highp float; uniform mat4 mvpMatrix; attribute vec4 inPosition; attribute vec2 inTexcoord; attribute float alpha; varying vec2 varTexcoord; varying float varIntensity; void main (void) { gl_Position = mvpMatrix * inPosition; varTexcoord = inTexcoord; varIntensity = alpha; }";
    private static final String PAINT_COMPOSITEWITHMASKLIGHT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); vec3 maskColor = texture2D(mask, varTexcoord.st, 0.0).rgb; float srcAlpha = clamp(0.78 * maskColor.r + maskColor.b + maskColor.g, 0.0, 1.0); vec3 borderColor = mix(color.rgb, vec3(1.0, 1.0, 1.0), 0.86); vec3 finalColor = mix(color.rgb, borderColor, maskColor.g); finalColor = mix(finalColor.rgb, vec3(1.0, 1.0, 1.0), maskColor.b); float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (finalColor * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_COMPOSITEWITHMASK_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; uniform sampler2D mask; uniform vec4 color; void main(void) { vec4 dst = texture2D(texture, varTexcoord.st, 0.0); float srcAlpha = color.a * texture2D(mask, varTexcoord.st, 0.0).a; float outAlpha = srcAlpha + dst.a * (1.0 - srcAlpha); gl_FragColor.rgb = (color.rgb * srcAlpha + dst.rgb * dst.a * (1.0 - srcAlpha)) / outAlpha; gl_FragColor.a = outAlpha; }";
    private static final String PAINT_NONPREMULTIPLIEDBLIT_FSH = "precision highp float; varying vec2 varTexcoord; uniform sampler2D texture; void main (void) { gl_FragColor = texture2D(texture, varTexcoord.st, 0.0); }";
    private static final String UNIFORMS = "uniforms";
    private static final String VERTEX = "vertex";

    private static Map<String, Map<String, Object>> createMap() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        Map<String, Object> shader = new HashMap<>();
        shader.put(VERTEX, PAINT_BRUSH_VSH);
        shader.put(FRAGMENT, PAINT_BRUSH_FSH);
        shader.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord", "alpha"});
        shader.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        result.put("brush", Collections.unmodifiableMap(shader));
        Map<String, Object> shader2 = new HashMap<>();
        shader2.put(VERTEX, PAINT_BRUSH_VSH);
        shader2.put(FRAGMENT, PAINT_BRUSHLIGHT_FSH);
        shader2.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord", "alpha"});
        shader2.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        result.put("brushLight", Collections.unmodifiableMap(shader2));
        Map<String, Object> shader3 = new HashMap<>();
        shader3.put(VERTEX, PAINT_BLIT_VSH);
        shader3.put(FRAGMENT, PAINT_BLIT_FSH);
        shader3.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader3.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        result.put("blit", Collections.unmodifiableMap(shader3));
        Map<String, Object> shader4 = new HashMap<>();
        shader4.put(VERTEX, PAINT_BLIT_VSH);
        shader4.put(FRAGMENT, PAINT_BLITWITHMASKLIGHT_FSH);
        shader4.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader4.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        result.put("blitWithMaskLight", Collections.unmodifiableMap(shader4));
        Map<String, Object> shader5 = new HashMap<>();
        shader5.put(VERTEX, PAINT_BLIT_VSH);
        shader5.put(FRAGMENT, PAINT_BLITWITHMASK_FSH);
        shader5.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader5.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        result.put("blitWithMask", Collections.unmodifiableMap(shader5));
        Map<String, Object> shader6 = new HashMap<>();
        shader6.put(VERTEX, PAINT_BLIT_VSH);
        shader6.put(FRAGMENT, PAINT_COMPOSITEWITHMASK_FSH);
        shader6.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader6.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        result.put("compositeWithMask", Collections.unmodifiableMap(shader6));
        Map<String, Object> shader7 = new HashMap<>();
        shader7.put(VERTEX, PAINT_BLIT_VSH);
        shader7.put(FRAGMENT, PAINT_COMPOSITEWITHMASKLIGHT_FSH);
        shader7.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader7.put(UNIFORMS, new String[]{"mvpMatrix", "texture", "mask", TtmlNode.ATTR_TTS_COLOR});
        result.put("compositeWithMaskLight", Collections.unmodifiableMap(shader7));
        Map<String, Object> shader8 = new HashMap<>();
        shader8.put(VERTEX, PAINT_BLIT_VSH);
        shader8.put(FRAGMENT, PAINT_NONPREMULTIPLIEDBLIT_FSH);
        shader8.put(ATTRIBUTES, new String[]{"inPosition", "inTexcoord"});
        shader8.put(UNIFORMS, new String[]{"mvpMatrix", "texture"});
        result.put("nonPremultipliedBlit", Collections.unmodifiableMap(shader8));
        return Collections.unmodifiableMap(result);
    }

    public static Map<String, Shader> setup() {
        Map<String, Shader> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : AVAILBALBE_SHADERS.entrySet()) {
            Map<String, Object> value = entry.getValue();
            String vertex = (String) value.get(VERTEX);
            String fragment = (String) value.get(FRAGMENT);
            String[] attributes = (String[]) value.get(ATTRIBUTES);
            String[] uniforms = (String[]) value.get(UNIFORMS);
            Shader shader = new Shader(vertex, fragment, attributes, uniforms);
            result.put(entry.getKey(), shader);
        }
        return Collections.unmodifiableMap(result);
    }
}
