#ifdef GL_ES
precision highp float;
#endif

#extension GL_OES_standard_derivatives : enable

uniform float time;
uniform vec2 mouse;
uniform vec2 resolution;

void main( void ) {

	vec2 st = gl_FragCoord.xy/resolution;
	float xx = st.x + time/20.;
	float height = (st.y - .5*sin(xx * 10.));
	float high_limit = 0.9 - 0.05*cos(2.*time + 7.);
	float low_limit = .1 + .05*cos(3.*time);
	if (height > low_limit && height < high_limit) {
		gl_FragColor = vec4(st.x, st.y, .7 + .2*sin(3.*time), 1);
	} else {
		gl_FragColor = vec4(st.y, st.x, 0, 1);
	}



}