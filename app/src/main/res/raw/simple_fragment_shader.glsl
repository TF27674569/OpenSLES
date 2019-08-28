//片段着色器用来告诉gpu 每个片段的最终颜色时什么
precision mediump float;//mediump 设置为中等精度 lowp 低精度   highp 高精度（性能低） 权衡速度与质量选择中等精度

varying vec4 v_Color;//vec4 4分量  r g b a

void main()
{
    gl_FragColor = v_Color;
}