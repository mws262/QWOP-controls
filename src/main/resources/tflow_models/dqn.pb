
?
inputPlaceholder*
shape:ÿÿÿÿÿÿÿÿÿ*
dtype0
G
output_targetPlaceholder*
dtype0*
shape:ÿÿÿÿÿÿÿÿÿ
C
scalar_targetPlaceholder*
dtype0*
shape:ÿÿÿÿÿÿÿÿÿ
b
-fully_connected0/weights/random_uniform/shapeConst*
valueB"      *
dtype0
X
+fully_connected0/weights/random_uniform/minConst*
valueB
 *øKF¿*
dtype0
X
+fully_connected0/weights/random_uniform/maxConst*
valueB
 *øKF?*
dtype0
¤
5fully_connected0/weights/random_uniform/RandomUniformRandomUniform-fully_connected0/weights/random_uniform/shape*

seed *
T0*
dtype0*
seed2 

+fully_connected0/weights/random_uniform/subSub+fully_connected0/weights/random_uniform/max+fully_connected0/weights/random_uniform/min*
T0

+fully_connected0/weights/random_uniform/mulMul5fully_connected0/weights/random_uniform/RandomUniform+fully_connected0/weights/random_uniform/sub*
T0

'fully_connected0/weights/random_uniformAdd+fully_connected0/weights/random_uniform/mul+fully_connected0/weights/random_uniform/min*
T0
s
fully_connected0/weights/weight
VariableV2*
shared_name *
dtype0*
	container *
shape
:
à
&fully_connected0/weights/weight/AssignAssignfully_connected0/weights/weight'fully_connected0/weights/random_uniform*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(

$fully_connected0/weights/weight/readIdentityfully_connected0/weights/weight*
T0*2
_class(
&$loc:@fully_connected0/weights/weight
Q
'fully_connected0/weights/summaries/RankConst*
value	B :*
dtype0
X
.fully_connected0/weights/summaries/range/startConst*
value	B : *
dtype0
X
.fully_connected0/weights/summaries/range/deltaConst*
value	B :*
dtype0
Æ
(fully_connected0/weights/summaries/rangeRange.fully_connected0/weights/summaries/range/start'fully_connected0/weights/summaries/Rank.fully_connected0/weights/summaries/range/delta*

Tidx0
¥
'fully_connected0/weights/summaries/MeanMean$fully_connected0/weights/weight/read(fully_connected0/weights/summaries/range*

Tidx0*
	keep_dims( *
T0

.fully_connected0/weights/summaries/mean_1/tagsConst*:
value1B/ B)fully_connected0/weights/summaries/mean_1*
dtype0

)fully_connected0/weights/summaries/mean_1ScalarSummary.fully_connected0/weights/summaries/mean_1/tags'fully_connected0/weights/summaries/Mean*
T0

-fully_connected0/weights/summaries/stddev/subSub$fully_connected0/weights/weight/read'fully_connected0/weights/summaries/Mean*
T0
r
0fully_connected0/weights/summaries/stddev/SquareSquare-fully_connected0/weights/summaries/stddev/sub*
T0
d
/fully_connected0/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
¿
.fully_connected0/weights/summaries/stddev/MeanMean0fully_connected0/weights/summaries/stddev/Square/fully_connected0/weights/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
o
.fully_connected0/weights/summaries/stddev/SqrtSqrt.fully_connected0/weights/summaries/stddev/Mean*
T0

5fully_connected0/weights/summaries/stddev/stddev/tagsConst*A
value8B6 B0fully_connected0/weights/summaries/stddev/stddev*
dtype0
±
0fully_connected0/weights/summaries/stddev/stddevScalarSummary5fully_connected0/weights/summaries/stddev/stddev/tags.fully_connected0/weights/summaries/stddev/Sqrt*
T0
X
.fully_connected0/weights/summaries/stddev/RankConst*
value	B :*
dtype0
_
5fully_connected0/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
_
5fully_connected0/weights/summaries/stddev/range/deltaConst*
value	B :*
dtype0
â
/fully_connected0/weights/summaries/stddev/rangeRange5fully_connected0/weights/summaries/stddev/range/start.fully_connected0/weights/summaries/stddev/Rank5fully_connected0/weights/summaries/stddev/range/delta*

Tidx0
±
-fully_connected0/weights/summaries/stddev/MaxMax$fully_connected0/weights/weight/read/fully_connected0/weights/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0

4fully_connected0/weights/summaries/stddev/max_1/tagsConst*@
value7B5 B/fully_connected0/weights/summaries/stddev/max_1*
dtype0
®
/fully_connected0/weights/summaries/stddev/max_1ScalarSummary4fully_connected0/weights/summaries/stddev/max_1/tags-fully_connected0/weights/summaries/stddev/Max*
T0
Z
0fully_connected0/weights/summaries/stddev/Rank_1Const*
value	B :*
dtype0
a
7fully_connected0/weights/summaries/stddev/range_1/startConst*
value	B : *
dtype0
a
7fully_connected0/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
ê
1fully_connected0/weights/summaries/stddev/range_1Range7fully_connected0/weights/summaries/stddev/range_1/start0fully_connected0/weights/summaries/stddev/Rank_17fully_connected0/weights/summaries/stddev/range_1/delta*

Tidx0
³
-fully_connected0/weights/summaries/stddev/MinMin$fully_connected0/weights/weight/read1fully_connected0/weights/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0

4fully_connected0/weights/summaries/stddev/min_1/tagsConst*@
value7B5 B/fully_connected0/weights/summaries/stddev/min_1*
dtype0
®
/fully_connected0/weights/summaries/stddev/min_1ScalarSummary4fully_connected0/weights/summaries/stddev/min_1/tags-fully_connected0/weights/summaries/stddev/Min*
T0

7fully_connected0/weights/summaries/stddev/histogram/tagConst*D
value;B9 B3fully_connected0/weights/summaries/stddev/histogram*
dtype0
¯
3fully_connected0/weights/summaries/stddev/histogramHistogramSummary7fully_connected0/weights/summaries/stddev/histogram/tag$fully_connected0/weights/weight/read*
T0
Z
,fully_connected0/biases/random_uniform/shapeConst*
valueB:*
dtype0
W
*fully_connected0/biases/random_uniform/minConst*
valueB
 *ó5¿*
dtype0
W
*fully_connected0/biases/random_uniform/maxConst*
valueB
 *ó5?*
dtype0
¢
4fully_connected0/biases/random_uniform/RandomUniformRandomUniform,fully_connected0/biases/random_uniform/shape*

seed *
T0*
dtype0*
seed2 

*fully_connected0/biases/random_uniform/subSub*fully_connected0/biases/random_uniform/max*fully_connected0/biases/random_uniform/min*
T0

*fully_connected0/biases/random_uniform/mulMul4fully_connected0/biases/random_uniform/RandomUniform*fully_connected0/biases/random_uniform/sub*
T0

&fully_connected0/biases/random_uniformAdd*fully_connected0/biases/random_uniform/mul*fully_connected0/biases/random_uniform/min*
T0
l
fully_connected0/biases/bias
VariableV2*
shape:*
shared_name *
dtype0*
	container 
Ö
#fully_connected0/biases/bias/AssignAssignfully_connected0/biases/bias&fully_connected0/biases/random_uniform*
use_locking(*
T0*/
_class%
#!loc:@fully_connected0/biases/bias*
validate_shape(

!fully_connected0/biases/bias/readIdentityfully_connected0/biases/bias*
T0*/
_class%
#!loc:@fully_connected0/biases/bias
P
&fully_connected0/biases/summaries/RankConst*
value	B :*
dtype0
W
-fully_connected0/biases/summaries/range/startConst*
dtype0*
value	B : 
W
-fully_connected0/biases/summaries/range/deltaConst*
value	B :*
dtype0
Â
'fully_connected0/biases/summaries/rangeRange-fully_connected0/biases/summaries/range/start&fully_connected0/biases/summaries/Rank-fully_connected0/biases/summaries/range/delta*

Tidx0
 
&fully_connected0/biases/summaries/MeanMean!fully_connected0/biases/bias/read'fully_connected0/biases/summaries/range*
T0*

Tidx0*
	keep_dims( 
~
-fully_connected0/biases/summaries/mean_1/tagsConst*9
value0B. B(fully_connected0/biases/summaries/mean_1*
dtype0

(fully_connected0/biases/summaries/mean_1ScalarSummary-fully_connected0/biases/summaries/mean_1/tags&fully_connected0/biases/summaries/Mean*
T0

,fully_connected0/biases/summaries/stddev/subSub!fully_connected0/biases/bias/read&fully_connected0/biases/summaries/Mean*
T0
p
/fully_connected0/biases/summaries/stddev/SquareSquare,fully_connected0/biases/summaries/stddev/sub*
T0
\
.fully_connected0/biases/summaries/stddev/ConstConst*
valueB: *
dtype0
¼
-fully_connected0/biases/summaries/stddev/MeanMean/fully_connected0/biases/summaries/stddev/Square.fully_connected0/biases/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
m
-fully_connected0/biases/summaries/stddev/SqrtSqrt-fully_connected0/biases/summaries/stddev/Mean*
T0

4fully_connected0/biases/summaries/stddev/stddev/tagsConst*@
value7B5 B/fully_connected0/biases/summaries/stddev/stddev*
dtype0
®
/fully_connected0/biases/summaries/stddev/stddevScalarSummary4fully_connected0/biases/summaries/stddev/stddev/tags-fully_connected0/biases/summaries/stddev/Sqrt*
T0
W
-fully_connected0/biases/summaries/stddev/RankConst*
value	B :*
dtype0
^
4fully_connected0/biases/summaries/stddev/range/startConst*
value	B : *
dtype0
^
4fully_connected0/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Þ
.fully_connected0/biases/summaries/stddev/rangeRange4fully_connected0/biases/summaries/stddev/range/start-fully_connected0/biases/summaries/stddev/Rank4fully_connected0/biases/summaries/stddev/range/delta*

Tidx0
¬
,fully_connected0/biases/summaries/stddev/MaxMax!fully_connected0/biases/bias/read.fully_connected0/biases/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 

3fully_connected0/biases/summaries/stddev/max_1/tagsConst*?
value6B4 B.fully_connected0/biases/summaries/stddev/max_1*
dtype0
«
.fully_connected0/biases/summaries/stddev/max_1ScalarSummary3fully_connected0/biases/summaries/stddev/max_1/tags,fully_connected0/biases/summaries/stddev/Max*
T0
Y
/fully_connected0/biases/summaries/stddev/Rank_1Const*
value	B :*
dtype0
`
6fully_connected0/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
`
6fully_connected0/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
æ
0fully_connected0/biases/summaries/stddev/range_1Range6fully_connected0/biases/summaries/stddev/range_1/start/fully_connected0/biases/summaries/stddev/Rank_16fully_connected0/biases/summaries/stddev/range_1/delta*

Tidx0
®
,fully_connected0/biases/summaries/stddev/MinMin!fully_connected0/biases/bias/read0fully_connected0/biases/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 

3fully_connected0/biases/summaries/stddev/min_1/tagsConst*?
value6B4 B.fully_connected0/biases/summaries/stddev/min_1*
dtype0
«
.fully_connected0/biases/summaries/stddev/min_1ScalarSummary3fully_connected0/biases/summaries/stddev/min_1/tags,fully_connected0/biases/summaries/stddev/Min*
T0

6fully_connected0/biases/summaries/stddev/histogram/tagConst*C
value:B8 B2fully_connected0/biases/summaries/stddev/histogram*
dtype0
ª
2fully_connected0/biases/summaries/stddev/histogramHistogramSummary6fully_connected0/biases/summaries/stddev/histogram/tag!fully_connected0/biases/bias/read*
T0

!fully_connected0/Wx_plus_b/MatMulMatMulinput$fully_connected0/weights/weight/read*
transpose_b( *
T0*
transpose_a( 
t
fully_connected0/Wx_plus_b/addAdd!fully_connected0/Wx_plus_b/MatMul!fully_connected0/biases/bias/read*
T0

.fully_connected0/Wx_plus_b/pre_activations/tagConst*;
value2B0 B*fully_connected0/Wx_plus_b/pre_activations*
dtype0

*fully_connected0/Wx_plus_b/pre_activationsHistogramSummary.fully_connected0/Wx_plus_b/pre_activations/tagfully_connected0/Wx_plus_b/add*
T0
K
fully_connected0/activationElufully_connected0/Wx_plus_b/add*
T0
e
 fully_connected0/activations/tagConst*-
value$B" Bfully_connected0/activations*
dtype0
x
fully_connected0/activationsHistogramSummary fully_connected0/activations/tagfully_connected0/activation*
T0
b
-fully_connected1/weights/random_uniform/shapeConst*
valueB"      *
dtype0
X
+fully_connected1/weights/random_uniform/minConst*
valueB
 *øKF¿*
dtype0
X
+fully_connected1/weights/random_uniform/maxConst*
valueB
 *øKF?*
dtype0
¤
5fully_connected1/weights/random_uniform/RandomUniformRandomUniform-fully_connected1/weights/random_uniform/shape*

seed *
T0*
dtype0*
seed2 

+fully_connected1/weights/random_uniform/subSub+fully_connected1/weights/random_uniform/max+fully_connected1/weights/random_uniform/min*
T0

+fully_connected1/weights/random_uniform/mulMul5fully_connected1/weights/random_uniform/RandomUniform+fully_connected1/weights/random_uniform/sub*
T0

'fully_connected1/weights/random_uniformAdd+fully_connected1/weights/random_uniform/mul+fully_connected1/weights/random_uniform/min*
T0
s
fully_connected1/weights/weight
VariableV2*
dtype0*
	container *
shape
:*
shared_name 
à
&fully_connected1/weights/weight/AssignAssignfully_connected1/weights/weight'fully_connected1/weights/random_uniform*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight*
validate_shape(

$fully_connected1/weights/weight/readIdentityfully_connected1/weights/weight*
T0*2
_class(
&$loc:@fully_connected1/weights/weight
Q
'fully_connected1/weights/summaries/RankConst*
dtype0*
value	B :
X
.fully_connected1/weights/summaries/range/startConst*
value	B : *
dtype0
X
.fully_connected1/weights/summaries/range/deltaConst*
dtype0*
value	B :
Æ
(fully_connected1/weights/summaries/rangeRange.fully_connected1/weights/summaries/range/start'fully_connected1/weights/summaries/Rank.fully_connected1/weights/summaries/range/delta*

Tidx0
¥
'fully_connected1/weights/summaries/MeanMean$fully_connected1/weights/weight/read(fully_connected1/weights/summaries/range*
T0*

Tidx0*
	keep_dims( 

.fully_connected1/weights/summaries/mean_1/tagsConst*:
value1B/ B)fully_connected1/weights/summaries/mean_1*
dtype0

)fully_connected1/weights/summaries/mean_1ScalarSummary.fully_connected1/weights/summaries/mean_1/tags'fully_connected1/weights/summaries/Mean*
T0

-fully_connected1/weights/summaries/stddev/subSub$fully_connected1/weights/weight/read'fully_connected1/weights/summaries/Mean*
T0
r
0fully_connected1/weights/summaries/stddev/SquareSquare-fully_connected1/weights/summaries/stddev/sub*
T0
d
/fully_connected1/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
¿
.fully_connected1/weights/summaries/stddev/MeanMean0fully_connected1/weights/summaries/stddev/Square/fully_connected1/weights/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
o
.fully_connected1/weights/summaries/stddev/SqrtSqrt.fully_connected1/weights/summaries/stddev/Mean*
T0

5fully_connected1/weights/summaries/stddev/stddev/tagsConst*A
value8B6 B0fully_connected1/weights/summaries/stddev/stddev*
dtype0
±
0fully_connected1/weights/summaries/stddev/stddevScalarSummary5fully_connected1/weights/summaries/stddev/stddev/tags.fully_connected1/weights/summaries/stddev/Sqrt*
T0
X
.fully_connected1/weights/summaries/stddev/RankConst*
value	B :*
dtype0
_
5fully_connected1/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
_
5fully_connected1/weights/summaries/stddev/range/deltaConst*
value	B :*
dtype0
â
/fully_connected1/weights/summaries/stddev/rangeRange5fully_connected1/weights/summaries/stddev/range/start.fully_connected1/weights/summaries/stddev/Rank5fully_connected1/weights/summaries/stddev/range/delta*

Tidx0
±
-fully_connected1/weights/summaries/stddev/MaxMax$fully_connected1/weights/weight/read/fully_connected1/weights/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0

4fully_connected1/weights/summaries/stddev/max_1/tagsConst*@
value7B5 B/fully_connected1/weights/summaries/stddev/max_1*
dtype0
®
/fully_connected1/weights/summaries/stddev/max_1ScalarSummary4fully_connected1/weights/summaries/stddev/max_1/tags-fully_connected1/weights/summaries/stddev/Max*
T0
Z
0fully_connected1/weights/summaries/stddev/Rank_1Const*
dtype0*
value	B :
a
7fully_connected1/weights/summaries/stddev/range_1/startConst*
value	B : *
dtype0
a
7fully_connected1/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
ê
1fully_connected1/weights/summaries/stddev/range_1Range7fully_connected1/weights/summaries/stddev/range_1/start0fully_connected1/weights/summaries/stddev/Rank_17fully_connected1/weights/summaries/stddev/range_1/delta*

Tidx0
³
-fully_connected1/weights/summaries/stddev/MinMin$fully_connected1/weights/weight/read1fully_connected1/weights/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0

4fully_connected1/weights/summaries/stddev/min_1/tagsConst*@
value7B5 B/fully_connected1/weights/summaries/stddev/min_1*
dtype0
®
/fully_connected1/weights/summaries/stddev/min_1ScalarSummary4fully_connected1/weights/summaries/stddev/min_1/tags-fully_connected1/weights/summaries/stddev/Min*
T0

7fully_connected1/weights/summaries/stddev/histogram/tagConst*D
value;B9 B3fully_connected1/weights/summaries/stddev/histogram*
dtype0
¯
3fully_connected1/weights/summaries/stddev/histogramHistogramSummary7fully_connected1/weights/summaries/stddev/histogram/tag$fully_connected1/weights/weight/read*
T0
Z
,fully_connected1/biases/random_uniform/shapeConst*
valueB:*
dtype0
W
*fully_connected1/biases/random_uniform/minConst*
valueB
 *×³]¿*
dtype0
W
*fully_connected1/biases/random_uniform/maxConst*
valueB
 *×³]?*
dtype0
¢
4fully_connected1/biases/random_uniform/RandomUniformRandomUniform,fully_connected1/biases/random_uniform/shape*

seed *
T0*
dtype0*
seed2 

*fully_connected1/biases/random_uniform/subSub*fully_connected1/biases/random_uniform/max*fully_connected1/biases/random_uniform/min*
T0

*fully_connected1/biases/random_uniform/mulMul4fully_connected1/biases/random_uniform/RandomUniform*fully_connected1/biases/random_uniform/sub*
T0

&fully_connected1/biases/random_uniformAdd*fully_connected1/biases/random_uniform/mul*fully_connected1/biases/random_uniform/min*
T0
l
fully_connected1/biases/bias
VariableV2*
shared_name *
dtype0*
	container *
shape:
Ö
#fully_connected1/biases/bias/AssignAssignfully_connected1/biases/bias&fully_connected1/biases/random_uniform*
use_locking(*
T0*/
_class%
#!loc:@fully_connected1/biases/bias*
validate_shape(

!fully_connected1/biases/bias/readIdentityfully_connected1/biases/bias*
T0*/
_class%
#!loc:@fully_connected1/biases/bias
P
&fully_connected1/biases/summaries/RankConst*
value	B :*
dtype0
W
-fully_connected1/biases/summaries/range/startConst*
value	B : *
dtype0
W
-fully_connected1/biases/summaries/range/deltaConst*
value	B :*
dtype0
Â
'fully_connected1/biases/summaries/rangeRange-fully_connected1/biases/summaries/range/start&fully_connected1/biases/summaries/Rank-fully_connected1/biases/summaries/range/delta*

Tidx0
 
&fully_connected1/biases/summaries/MeanMean!fully_connected1/biases/bias/read'fully_connected1/biases/summaries/range*
T0*

Tidx0*
	keep_dims( 
~
-fully_connected1/biases/summaries/mean_1/tagsConst*9
value0B. B(fully_connected1/biases/summaries/mean_1*
dtype0

(fully_connected1/biases/summaries/mean_1ScalarSummary-fully_connected1/biases/summaries/mean_1/tags&fully_connected1/biases/summaries/Mean*
T0

,fully_connected1/biases/summaries/stddev/subSub!fully_connected1/biases/bias/read&fully_connected1/biases/summaries/Mean*
T0
p
/fully_connected1/biases/summaries/stddev/SquareSquare,fully_connected1/biases/summaries/stddev/sub*
T0
\
.fully_connected1/biases/summaries/stddev/ConstConst*
dtype0*
valueB: 
¼
-fully_connected1/biases/summaries/stddev/MeanMean/fully_connected1/biases/summaries/stddev/Square.fully_connected1/biases/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
m
-fully_connected1/biases/summaries/stddev/SqrtSqrt-fully_connected1/biases/summaries/stddev/Mean*
T0

4fully_connected1/biases/summaries/stddev/stddev/tagsConst*
dtype0*@
value7B5 B/fully_connected1/biases/summaries/stddev/stddev
®
/fully_connected1/biases/summaries/stddev/stddevScalarSummary4fully_connected1/biases/summaries/stddev/stddev/tags-fully_connected1/biases/summaries/stddev/Sqrt*
T0
W
-fully_connected1/biases/summaries/stddev/RankConst*
value	B :*
dtype0
^
4fully_connected1/biases/summaries/stddev/range/startConst*
value	B : *
dtype0
^
4fully_connected1/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Þ
.fully_connected1/biases/summaries/stddev/rangeRange4fully_connected1/biases/summaries/stddev/range/start-fully_connected1/biases/summaries/stddev/Rank4fully_connected1/biases/summaries/stddev/range/delta*

Tidx0
¬
,fully_connected1/biases/summaries/stddev/MaxMax!fully_connected1/biases/bias/read.fully_connected1/biases/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0

3fully_connected1/biases/summaries/stddev/max_1/tagsConst*?
value6B4 B.fully_connected1/biases/summaries/stddev/max_1*
dtype0
«
.fully_connected1/biases/summaries/stddev/max_1ScalarSummary3fully_connected1/biases/summaries/stddev/max_1/tags,fully_connected1/biases/summaries/stddev/Max*
T0
Y
/fully_connected1/biases/summaries/stddev/Rank_1Const*
value	B :*
dtype0
`
6fully_connected1/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
`
6fully_connected1/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
æ
0fully_connected1/biases/summaries/stddev/range_1Range6fully_connected1/biases/summaries/stddev/range_1/start/fully_connected1/biases/summaries/stddev/Rank_16fully_connected1/biases/summaries/stddev/range_1/delta*

Tidx0
®
,fully_connected1/biases/summaries/stddev/MinMin!fully_connected1/biases/bias/read0fully_connected1/biases/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 

3fully_connected1/biases/summaries/stddev/min_1/tagsConst*?
value6B4 B.fully_connected1/biases/summaries/stddev/min_1*
dtype0
«
.fully_connected1/biases/summaries/stddev/min_1ScalarSummary3fully_connected1/biases/summaries/stddev/min_1/tags,fully_connected1/biases/summaries/stddev/Min*
T0

6fully_connected1/biases/summaries/stddev/histogram/tagConst*C
value:B8 B2fully_connected1/biases/summaries/stddev/histogram*
dtype0
ª
2fully_connected1/biases/summaries/stddev/histogramHistogramSummary6fully_connected1/biases/summaries/stddev/histogram/tag!fully_connected1/biases/bias/read*
T0

!fully_connected1/Wx_plus_b/MatMulMatMulfully_connected0/activation$fully_connected1/weights/weight/read*
T0*
transpose_a( *
transpose_b( 
t
fully_connected1/Wx_plus_b/addAdd!fully_connected1/Wx_plus_b/MatMul!fully_connected1/biases/bias/read*
T0

.fully_connected1/Wx_plus_b/pre_activations/tagConst*
dtype0*;
value2B0 B*fully_connected1/Wx_plus_b/pre_activations

*fully_connected1/Wx_plus_b/pre_activationsHistogramSummary.fully_connected1/Wx_plus_b/pre_activations/tagfully_connected1/Wx_plus_b/add*
T0
K
fully_connected1/activationElufully_connected1/Wx_plus_b/add*
T0
e
 fully_connected1/activations/tagConst*-
value$B" Bfully_connected1/activations*
dtype0
x
fully_connected1/activationsHistogramSummary fully_connected1/activations/tagfully_connected1/activation*
T0
X
#value0/weights/random_uniform/shapeConst*
valueB"      *
dtype0
N
!value0/weights/random_uniform/minConst*
valueB
 *  ¿*
dtype0
N
!value0/weights/random_uniform/maxConst*
valueB
 *  ?*
dtype0

+value0/weights/random_uniform/RandomUniformRandomUniform#value0/weights/random_uniform/shape*
dtype0*
seed2 *

seed *
T0
w
!value0/weights/random_uniform/subSub!value0/weights/random_uniform/max!value0/weights/random_uniform/min*
T0

!value0/weights/random_uniform/mulMul+value0/weights/random_uniform/RandomUniform!value0/weights/random_uniform/sub*
T0
s
value0/weights/random_uniformAdd!value0/weights/random_uniform/mul!value0/weights/random_uniform/min*
T0
i
value0/weights/weight
VariableV2*
shape
:*
shared_name *
dtype0*
	container 
¸
value0/weights/weight/AssignAssignvalue0/weights/weightvalue0/weights/random_uniform*
use_locking(*
T0*(
_class
loc:@value0/weights/weight*
validate_shape(
p
value0/weights/weight/readIdentityvalue0/weights/weight*
T0*(
_class
loc:@value0/weights/weight
G
value0/weights/summaries/RankConst*
value	B :*
dtype0
N
$value0/weights/summaries/range/startConst*
value	B : *
dtype0
N
$value0/weights/summaries/range/deltaConst*
value	B :*
dtype0

value0/weights/summaries/rangeRange$value0/weights/summaries/range/startvalue0/weights/summaries/Rank$value0/weights/summaries/range/delta*

Tidx0

value0/weights/summaries/MeanMeanvalue0/weights/weight/readvalue0/weights/summaries/range*
T0*

Tidx0*
	keep_dims( 
l
$value0/weights/summaries/mean_1/tagsConst*0
value'B% Bvalue0/weights/summaries/mean_1*
dtype0
~
value0/weights/summaries/mean_1ScalarSummary$value0/weights/summaries/mean_1/tagsvalue0/weights/summaries/Mean*
T0
n
#value0/weights/summaries/stddev/subSubvalue0/weights/weight/readvalue0/weights/summaries/Mean*
T0
^
&value0/weights/summaries/stddev/SquareSquare#value0/weights/summaries/stddev/sub*
T0
Z
%value0/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
¡
$value0/weights/summaries/stddev/MeanMean&value0/weights/summaries/stddev/Square%value0/weights/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
[
$value0/weights/summaries/stddev/SqrtSqrt$value0/weights/summaries/stddev/Mean*
T0
z
+value0/weights/summaries/stddev/stddev/tagsConst*
dtype0*7
value.B, B&value0/weights/summaries/stddev/stddev

&value0/weights/summaries/stddev/stddevScalarSummary+value0/weights/summaries/stddev/stddev/tags$value0/weights/summaries/stddev/Sqrt*
T0
N
$value0/weights/summaries/stddev/RankConst*
value	B :*
dtype0
U
+value0/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
U
+value0/weights/summaries/stddev/range/deltaConst*
dtype0*
value	B :
º
%value0/weights/summaries/stddev/rangeRange+value0/weights/summaries/stddev/range/start$value0/weights/summaries/stddev/Rank+value0/weights/summaries/stddev/range/delta*

Tidx0

#value0/weights/summaries/stddev/MaxMaxvalue0/weights/weight/read%value0/weights/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 
x
*value0/weights/summaries/stddev/max_1/tagsConst*
dtype0*6
value-B+ B%value0/weights/summaries/stddev/max_1

%value0/weights/summaries/stddev/max_1ScalarSummary*value0/weights/summaries/stddev/max_1/tags#value0/weights/summaries/stddev/Max*
T0
P
&value0/weights/summaries/stddev/Rank_1Const*
dtype0*
value	B :
W
-value0/weights/summaries/stddev/range_1/startConst*
value	B : *
dtype0
W
-value0/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Â
'value0/weights/summaries/stddev/range_1Range-value0/weights/summaries/stddev/range_1/start&value0/weights/summaries/stddev/Rank_1-value0/weights/summaries/stddev/range_1/delta*

Tidx0

#value0/weights/summaries/stddev/MinMinvalue0/weights/weight/read'value0/weights/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 
x
*value0/weights/summaries/stddev/min_1/tagsConst*6
value-B+ B%value0/weights/summaries/stddev/min_1*
dtype0

%value0/weights/summaries/stddev/min_1ScalarSummary*value0/weights/summaries/stddev/min_1/tags#value0/weights/summaries/stddev/Min*
T0

-value0/weights/summaries/stddev/histogram/tagConst*:
value1B/ B)value0/weights/summaries/stddev/histogram*
dtype0

)value0/weights/summaries/stddev/histogramHistogramSummary-value0/weights/summaries/stddev/histogram/tagvalue0/weights/weight/read*
T0
P
"value0/biases/random_uniform/shapeConst*
valueB:*
dtype0
M
 value0/biases/random_uniform/minConst*
valueB
 *qÄ¿*
dtype0
M
 value0/biases/random_uniform/maxConst*
valueB
 *qÄ?*
dtype0

*value0/biases/random_uniform/RandomUniformRandomUniform"value0/biases/random_uniform/shape*
seed2 *

seed *
T0*
dtype0
t
 value0/biases/random_uniform/subSub value0/biases/random_uniform/max value0/biases/random_uniform/min*
T0
~
 value0/biases/random_uniform/mulMul*value0/biases/random_uniform/RandomUniform value0/biases/random_uniform/sub*
T0
p
value0/biases/random_uniformAdd value0/biases/random_uniform/mul value0/biases/random_uniform/min*
T0
b
value0/biases/bias
VariableV2*
	container *
shape:*
shared_name *
dtype0
®
value0/biases/bias/AssignAssignvalue0/biases/biasvalue0/biases/random_uniform*
use_locking(*
T0*%
_class
loc:@value0/biases/bias*
validate_shape(
g
value0/biases/bias/readIdentityvalue0/biases/bias*
T0*%
_class
loc:@value0/biases/bias
F
value0/biases/summaries/RankConst*
value	B :*
dtype0
M
#value0/biases/summaries/range/startConst*
dtype0*
value	B : 
M
#value0/biases/summaries/range/deltaConst*
value	B :*
dtype0

value0/biases/summaries/rangeRange#value0/biases/summaries/range/startvalue0/biases/summaries/Rank#value0/biases/summaries/range/delta*

Tidx0

value0/biases/summaries/MeanMeanvalue0/biases/bias/readvalue0/biases/summaries/range*

Tidx0*
	keep_dims( *
T0
j
#value0/biases/summaries/mean_1/tagsConst*/
value&B$ Bvalue0/biases/summaries/mean_1*
dtype0
{
value0/biases/summaries/mean_1ScalarSummary#value0/biases/summaries/mean_1/tagsvalue0/biases/summaries/Mean*
T0
i
"value0/biases/summaries/stddev/subSubvalue0/biases/bias/readvalue0/biases/summaries/Mean*
T0
\
%value0/biases/summaries/stddev/SquareSquare"value0/biases/summaries/stddev/sub*
T0
R
$value0/biases/summaries/stddev/ConstConst*
valueB: *
dtype0

#value0/biases/summaries/stddev/MeanMean%value0/biases/summaries/stddev/Square$value0/biases/summaries/stddev/Const*
T0*

Tidx0*
	keep_dims( 
Y
#value0/biases/summaries/stddev/SqrtSqrt#value0/biases/summaries/stddev/Mean*
T0
x
*value0/biases/summaries/stddev/stddev/tagsConst*6
value-B+ B%value0/biases/summaries/stddev/stddev*
dtype0

%value0/biases/summaries/stddev/stddevScalarSummary*value0/biases/summaries/stddev/stddev/tags#value0/biases/summaries/stddev/Sqrt*
T0
M
#value0/biases/summaries/stddev/RankConst*
value	B :*
dtype0
T
*value0/biases/summaries/stddev/range/startConst*
value	B : *
dtype0
T
*value0/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
¶
$value0/biases/summaries/stddev/rangeRange*value0/biases/summaries/stddev/range/start#value0/biases/summaries/stddev/Rank*value0/biases/summaries/stddev/range/delta*

Tidx0

"value0/biases/summaries/stddev/MaxMaxvalue0/biases/bias/read$value0/biases/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0
v
)value0/biases/summaries/stddev/max_1/tagsConst*5
value,B* B$value0/biases/summaries/stddev/max_1*
dtype0

$value0/biases/summaries/stddev/max_1ScalarSummary)value0/biases/summaries/stddev/max_1/tags"value0/biases/summaries/stddev/Max*
T0
O
%value0/biases/summaries/stddev/Rank_1Const*
value	B :*
dtype0
V
,value0/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
V
,value0/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
¾
&value0/biases/summaries/stddev/range_1Range,value0/biases/summaries/stddev/range_1/start%value0/biases/summaries/stddev/Rank_1,value0/biases/summaries/stddev/range_1/delta*

Tidx0

"value0/biases/summaries/stddev/MinMinvalue0/biases/bias/read&value0/biases/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 
v
)value0/biases/summaries/stddev/min_1/tagsConst*5
value,B* B$value0/biases/summaries/stddev/min_1*
dtype0

$value0/biases/summaries/stddev/min_1ScalarSummary)value0/biases/summaries/stddev/min_1/tags"value0/biases/summaries/stddev/Min*
T0
}
,value0/biases/summaries/stddev/histogram/tagConst*
dtype0*9
value0B. B(value0/biases/summaries/stddev/histogram

(value0/biases/summaries/stddev/histogramHistogramSummary,value0/biases/summaries/stddev/histogram/tagvalue0/biases/bias/read*
T0

value0/Wx_plus_b/MatMulMatMulfully_connected1/activationvalue0/weights/weight/read*
T0*
transpose_a( *
transpose_b( 
V
value0/Wx_plus_b/addAddvalue0/Wx_plus_b/MatMulvalue0/biases/bias/read*
T0
m
$value0/Wx_plus_b/pre_activations/tagConst*1
value(B& B value0/Wx_plus_b/pre_activations*
dtype0
y
 value0/Wx_plus_b/pre_activationsHistogramSummary$value0/Wx_plus_b/pre_activations/tagvalue0/Wx_plus_b/add*
T0
7
value0/activationEluvalue0/Wx_plus_b/add*
T0
Q
value0/activations/tagConst*#
valueB Bvalue0/activations*
dtype0
Z
value0/activationsHistogramSummaryvalue0/activations/tagvalue0/activation*
T0
X
#value1/weights/random_uniform/shapeConst*
valueB"      *
dtype0
N
!value1/weights/random_uniform/minConst*
valueB
 *óµ¿*
dtype0
N
!value1/weights/random_uniform/maxConst*
valueB
 *óµ?*
dtype0

+value1/weights/random_uniform/RandomUniformRandomUniform#value1/weights/random_uniform/shape*
dtype0*
seed2 *

seed *
T0
w
!value1/weights/random_uniform/subSub!value1/weights/random_uniform/max!value1/weights/random_uniform/min*
T0

!value1/weights/random_uniform/mulMul+value1/weights/random_uniform/RandomUniform!value1/weights/random_uniform/sub*
T0
s
value1/weights/random_uniformAdd!value1/weights/random_uniform/mul!value1/weights/random_uniform/min*
T0
i
value1/weights/weight
VariableV2*
dtype0*
	container *
shape
:*
shared_name 
¸
value1/weights/weight/AssignAssignvalue1/weights/weightvalue1/weights/random_uniform*
validate_shape(*
use_locking(*
T0*(
_class
loc:@value1/weights/weight
p
value1/weights/weight/readIdentityvalue1/weights/weight*
T0*(
_class
loc:@value1/weights/weight
G
value1/weights/summaries/RankConst*
value	B :*
dtype0
N
$value1/weights/summaries/range/startConst*
value	B : *
dtype0
N
$value1/weights/summaries/range/deltaConst*
value	B :*
dtype0

value1/weights/summaries/rangeRange$value1/weights/summaries/range/startvalue1/weights/summaries/Rank$value1/weights/summaries/range/delta*

Tidx0

value1/weights/summaries/MeanMeanvalue1/weights/weight/readvalue1/weights/summaries/range*

Tidx0*
	keep_dims( *
T0
l
$value1/weights/summaries/mean_1/tagsConst*0
value'B% Bvalue1/weights/summaries/mean_1*
dtype0
~
value1/weights/summaries/mean_1ScalarSummary$value1/weights/summaries/mean_1/tagsvalue1/weights/summaries/Mean*
T0
n
#value1/weights/summaries/stddev/subSubvalue1/weights/weight/readvalue1/weights/summaries/Mean*
T0
^
&value1/weights/summaries/stddev/SquareSquare#value1/weights/summaries/stddev/sub*
T0
Z
%value1/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
¡
$value1/weights/summaries/stddev/MeanMean&value1/weights/summaries/stddev/Square%value1/weights/summaries/stddev/Const*
T0*

Tidx0*
	keep_dims( 
[
$value1/weights/summaries/stddev/SqrtSqrt$value1/weights/summaries/stddev/Mean*
T0
z
+value1/weights/summaries/stddev/stddev/tagsConst*7
value.B, B&value1/weights/summaries/stddev/stddev*
dtype0

&value1/weights/summaries/stddev/stddevScalarSummary+value1/weights/summaries/stddev/stddev/tags$value1/weights/summaries/stddev/Sqrt*
T0
N
$value1/weights/summaries/stddev/RankConst*
value	B :*
dtype0
U
+value1/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
U
+value1/weights/summaries/stddev/range/deltaConst*
value	B :*
dtype0
º
%value1/weights/summaries/stddev/rangeRange+value1/weights/summaries/stddev/range/start$value1/weights/summaries/stddev/Rank+value1/weights/summaries/stddev/range/delta*

Tidx0

#value1/weights/summaries/stddev/MaxMaxvalue1/weights/weight/read%value1/weights/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 
x
*value1/weights/summaries/stddev/max_1/tagsConst*6
value-B+ B%value1/weights/summaries/stddev/max_1*
dtype0

%value1/weights/summaries/stddev/max_1ScalarSummary*value1/weights/summaries/stddev/max_1/tags#value1/weights/summaries/stddev/Max*
T0
P
&value1/weights/summaries/stddev/Rank_1Const*
value	B :*
dtype0
W
-value1/weights/summaries/stddev/range_1/startConst*
dtype0*
value	B : 
W
-value1/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Â
'value1/weights/summaries/stddev/range_1Range-value1/weights/summaries/stddev/range_1/start&value1/weights/summaries/stddev/Rank_1-value1/weights/summaries/stddev/range_1/delta*

Tidx0

#value1/weights/summaries/stddev/MinMinvalue1/weights/weight/read'value1/weights/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 
x
*value1/weights/summaries/stddev/min_1/tagsConst*6
value-B+ B%value1/weights/summaries/stddev/min_1*
dtype0

%value1/weights/summaries/stddev/min_1ScalarSummary*value1/weights/summaries/stddev/min_1/tags#value1/weights/summaries/stddev/Min*
T0

-value1/weights/summaries/stddev/histogram/tagConst*:
value1B/ B)value1/weights/summaries/stddev/histogram*
dtype0

)value1/weights/summaries/stddev/histogramHistogramSummary-value1/weights/summaries/stddev/histogram/tagvalue1/weights/weight/read*
T0
P
"value1/biases/random_uniform/shapeConst*
valueB:*
dtype0
M
 value1/biases/random_uniform/minConst*
valueB
 *×³Ý¿*
dtype0
M
 value1/biases/random_uniform/maxConst*
valueB
 *×³Ý?*
dtype0

*value1/biases/random_uniform/RandomUniformRandomUniform"value1/biases/random_uniform/shape*
dtype0*
seed2 *

seed *
T0
t
 value1/biases/random_uniform/subSub value1/biases/random_uniform/max value1/biases/random_uniform/min*
T0
~
 value1/biases/random_uniform/mulMul*value1/biases/random_uniform/RandomUniform value1/biases/random_uniform/sub*
T0
p
value1/biases/random_uniformAdd value1/biases/random_uniform/mul value1/biases/random_uniform/min*
T0
b
value1/biases/bias
VariableV2*
dtype0*
	container *
shape:*
shared_name 
®
value1/biases/bias/AssignAssignvalue1/biases/biasvalue1/biases/random_uniform*%
_class
loc:@value1/biases/bias*
validate_shape(*
use_locking(*
T0
g
value1/biases/bias/readIdentityvalue1/biases/bias*
T0*%
_class
loc:@value1/biases/bias
F
value1/biases/summaries/RankConst*
value	B :*
dtype0
M
#value1/biases/summaries/range/startConst*
value	B : *
dtype0
M
#value1/biases/summaries/range/deltaConst*
value	B :*
dtype0

value1/biases/summaries/rangeRange#value1/biases/summaries/range/startvalue1/biases/summaries/Rank#value1/biases/summaries/range/delta*

Tidx0

value1/biases/summaries/MeanMeanvalue1/biases/bias/readvalue1/biases/summaries/range*
T0*

Tidx0*
	keep_dims( 
j
#value1/biases/summaries/mean_1/tagsConst*/
value&B$ Bvalue1/biases/summaries/mean_1*
dtype0
{
value1/biases/summaries/mean_1ScalarSummary#value1/biases/summaries/mean_1/tagsvalue1/biases/summaries/Mean*
T0
i
"value1/biases/summaries/stddev/subSubvalue1/biases/bias/readvalue1/biases/summaries/Mean*
T0
\
%value1/biases/summaries/stddev/SquareSquare"value1/biases/summaries/stddev/sub*
T0
R
$value1/biases/summaries/stddev/ConstConst*
valueB: *
dtype0

#value1/biases/summaries/stddev/MeanMean%value1/biases/summaries/stddev/Square$value1/biases/summaries/stddev/Const*
T0*

Tidx0*
	keep_dims( 
Y
#value1/biases/summaries/stddev/SqrtSqrt#value1/biases/summaries/stddev/Mean*
T0
x
*value1/biases/summaries/stddev/stddev/tagsConst*6
value-B+ B%value1/biases/summaries/stddev/stddev*
dtype0

%value1/biases/summaries/stddev/stddevScalarSummary*value1/biases/summaries/stddev/stddev/tags#value1/biases/summaries/stddev/Sqrt*
T0
M
#value1/biases/summaries/stddev/RankConst*
value	B :*
dtype0
T
*value1/biases/summaries/stddev/range/startConst*
value	B : *
dtype0
T
*value1/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
¶
$value1/biases/summaries/stddev/rangeRange*value1/biases/summaries/stddev/range/start#value1/biases/summaries/stddev/Rank*value1/biases/summaries/stddev/range/delta*

Tidx0

"value1/biases/summaries/stddev/MaxMaxvalue1/biases/bias/read$value1/biases/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 
v
)value1/biases/summaries/stddev/max_1/tagsConst*5
value,B* B$value1/biases/summaries/stddev/max_1*
dtype0

$value1/biases/summaries/stddev/max_1ScalarSummary)value1/biases/summaries/stddev/max_1/tags"value1/biases/summaries/stddev/Max*
T0
O
%value1/biases/summaries/stddev/Rank_1Const*
dtype0*
value	B :
V
,value1/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
V
,value1/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
¾
&value1/biases/summaries/stddev/range_1Range,value1/biases/summaries/stddev/range_1/start%value1/biases/summaries/stddev/Rank_1,value1/biases/summaries/stddev/range_1/delta*

Tidx0

"value1/biases/summaries/stddev/MinMinvalue1/biases/bias/read&value1/biases/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0
v
)value1/biases/summaries/stddev/min_1/tagsConst*5
value,B* B$value1/biases/summaries/stddev/min_1*
dtype0

$value1/biases/summaries/stddev/min_1ScalarSummary)value1/biases/summaries/stddev/min_1/tags"value1/biases/summaries/stddev/Min*
T0
}
,value1/biases/summaries/stddev/histogram/tagConst*9
value0B. B(value1/biases/summaries/stddev/histogram*
dtype0

(value1/biases/summaries/stddev/histogramHistogramSummary,value1/biases/summaries/stddev/histogram/tagvalue1/biases/bias/read*
T0

value1/Wx_plus_b/MatMulMatMulvalue0/activationvalue1/weights/weight/read*
T0*
transpose_a( *
transpose_b( 
V
value1/Wx_plus_b/addAddvalue1/Wx_plus_b/MatMulvalue1/biases/bias/read*
T0
m
$value1/Wx_plus_b/pre_activations/tagConst*1
value(B& B value1/Wx_plus_b/pre_activations*
dtype0
y
 value1/Wx_plus_b/pre_activationsHistogramSummary$value1/Wx_plus_b/pre_activations/tagvalue1/Wx_plus_b/add*
T0
7
value1/activationEluvalue1/Wx_plus_b/add*
T0
Q
value1/activations/tagConst*#
valueB Bvalue1/activations*
dtype0
Z
value1/activationsHistogramSummaryvalue1/activations/tagvalue1/activation*
T0
\
'advantage0/weights/random_uniform/shapeConst*
valueB"      *
dtype0
R
%advantage0/weights/random_uniform/minConst*
valueB
 *×³]¿*
dtype0
R
%advantage0/weights/random_uniform/maxConst*
valueB
 *×³]?*
dtype0

/advantage0/weights/random_uniform/RandomUniformRandomUniform'advantage0/weights/random_uniform/shape*
T0*
dtype0*
seed2 *

seed 

%advantage0/weights/random_uniform/subSub%advantage0/weights/random_uniform/max%advantage0/weights/random_uniform/min*
T0

%advantage0/weights/random_uniform/mulMul/advantage0/weights/random_uniform/RandomUniform%advantage0/weights/random_uniform/sub*
T0

!advantage0/weights/random_uniformAdd%advantage0/weights/random_uniform/mul%advantage0/weights/random_uniform/min*
T0
m
advantage0/weights/weight
VariableV2*
shared_name *
dtype0*
	container *
shape
:
È
 advantage0/weights/weight/AssignAssignadvantage0/weights/weight!advantage0/weights/random_uniform*
use_locking(*
T0*,
_class"
 loc:@advantage0/weights/weight*
validate_shape(
|
advantage0/weights/weight/readIdentityadvantage0/weights/weight*,
_class"
 loc:@advantage0/weights/weight*
T0
K
!advantage0/weights/summaries/RankConst*
value	B :*
dtype0
R
(advantage0/weights/summaries/range/startConst*
dtype0*
value	B : 
R
(advantage0/weights/summaries/range/deltaConst*
value	B :*
dtype0
®
"advantage0/weights/summaries/rangeRange(advantage0/weights/summaries/range/start!advantage0/weights/summaries/Rank(advantage0/weights/summaries/range/delta*

Tidx0

!advantage0/weights/summaries/MeanMeanadvantage0/weights/weight/read"advantage0/weights/summaries/range*
T0*

Tidx0*
	keep_dims( 
t
(advantage0/weights/summaries/mean_1/tagsConst*4
value+B) B#advantage0/weights/summaries/mean_1*
dtype0

#advantage0/weights/summaries/mean_1ScalarSummary(advantage0/weights/summaries/mean_1/tags!advantage0/weights/summaries/Mean*
T0
z
'advantage0/weights/summaries/stddev/subSubadvantage0/weights/weight/read!advantage0/weights/summaries/Mean*
T0
f
*advantage0/weights/summaries/stddev/SquareSquare'advantage0/weights/summaries/stddev/sub*
T0
^
)advantage0/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
­
(advantage0/weights/summaries/stddev/MeanMean*advantage0/weights/summaries/stddev/Square)advantage0/weights/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
c
(advantage0/weights/summaries/stddev/SqrtSqrt(advantage0/weights/summaries/stddev/Mean*
T0

/advantage0/weights/summaries/stddev/stddev/tagsConst*;
value2B0 B*advantage0/weights/summaries/stddev/stddev*
dtype0

*advantage0/weights/summaries/stddev/stddevScalarSummary/advantage0/weights/summaries/stddev/stddev/tags(advantage0/weights/summaries/stddev/Sqrt*
T0
R
(advantage0/weights/summaries/stddev/RankConst*
value	B :*
dtype0
Y
/advantage0/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
Y
/advantage0/weights/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Ê
)advantage0/weights/summaries/stddev/rangeRange/advantage0/weights/summaries/stddev/range/start(advantage0/weights/summaries/stddev/Rank/advantage0/weights/summaries/stddev/range/delta*

Tidx0

'advantage0/weights/summaries/stddev/MaxMaxadvantage0/weights/weight/read)advantage0/weights/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0

.advantage0/weights/summaries/stddev/max_1/tagsConst*:
value1B/ B)advantage0/weights/summaries/stddev/max_1*
dtype0

)advantage0/weights/summaries/stddev/max_1ScalarSummary.advantage0/weights/summaries/stddev/max_1/tags'advantage0/weights/summaries/stddev/Max*
T0
T
*advantage0/weights/summaries/stddev/Rank_1Const*
value	B :*
dtype0
[
1advantage0/weights/summaries/stddev/range_1/startConst*
value	B : *
dtype0
[
1advantage0/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Ò
+advantage0/weights/summaries/stddev/range_1Range1advantage0/weights/summaries/stddev/range_1/start*advantage0/weights/summaries/stddev/Rank_11advantage0/weights/summaries/stddev/range_1/delta*

Tidx0
¡
'advantage0/weights/summaries/stddev/MinMinadvantage0/weights/weight/read+advantage0/weights/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0

.advantage0/weights/summaries/stddev/min_1/tagsConst*:
value1B/ B)advantage0/weights/summaries/stddev/min_1*
dtype0

)advantage0/weights/summaries/stddev/min_1ScalarSummary.advantage0/weights/summaries/stddev/min_1/tags'advantage0/weights/summaries/stddev/Min*
T0

1advantage0/weights/summaries/stddev/histogram/tagConst*>
value5B3 B-advantage0/weights/summaries/stddev/histogram*
dtype0

-advantage0/weights/summaries/stddev/histogramHistogramSummary1advantage0/weights/summaries/stddev/histogram/tagadvantage0/weights/weight/read*
T0
T
&advantage0/biases/random_uniform/shapeConst*
valueB:*
dtype0
Q
$advantage0/biases/random_uniform/minConst*
valueB
 *×³]¿*
dtype0
Q
$advantage0/biases/random_uniform/maxConst*
valueB
 *×³]?*
dtype0

.advantage0/biases/random_uniform/RandomUniformRandomUniform&advantage0/biases/random_uniform/shape*
dtype0*
seed2 *

seed *
T0

$advantage0/biases/random_uniform/subSub$advantage0/biases/random_uniform/max$advantage0/biases/random_uniform/min*
T0

$advantage0/biases/random_uniform/mulMul.advantage0/biases/random_uniform/RandomUniform$advantage0/biases/random_uniform/sub*
T0
|
 advantage0/biases/random_uniformAdd$advantage0/biases/random_uniform/mul$advantage0/biases/random_uniform/min*
T0
f
advantage0/biases/bias
VariableV2*
shape:*
shared_name *
dtype0*
	container 
¾
advantage0/biases/bias/AssignAssignadvantage0/biases/bias advantage0/biases/random_uniform*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(
s
advantage0/biases/bias/readIdentityadvantage0/biases/bias*
T0*)
_class
loc:@advantage0/biases/bias
J
 advantage0/biases/summaries/RankConst*
value	B :*
dtype0
Q
'advantage0/biases/summaries/range/startConst*
value	B : *
dtype0
Q
'advantage0/biases/summaries/range/deltaConst*
value	B :*
dtype0
ª
!advantage0/biases/summaries/rangeRange'advantage0/biases/summaries/range/start advantage0/biases/summaries/Rank'advantage0/biases/summaries/range/delta*

Tidx0

 advantage0/biases/summaries/MeanMeanadvantage0/biases/bias/read!advantage0/biases/summaries/range*

Tidx0*
	keep_dims( *
T0
r
'advantage0/biases/summaries/mean_1/tagsConst*3
value*B( B"advantage0/biases/summaries/mean_1*
dtype0

"advantage0/biases/summaries/mean_1ScalarSummary'advantage0/biases/summaries/mean_1/tags advantage0/biases/summaries/Mean*
T0
u
&advantage0/biases/summaries/stddev/subSubadvantage0/biases/bias/read advantage0/biases/summaries/Mean*
T0
d
)advantage0/biases/summaries/stddev/SquareSquare&advantage0/biases/summaries/stddev/sub*
T0
V
(advantage0/biases/summaries/stddev/ConstConst*
valueB: *
dtype0
ª
'advantage0/biases/summaries/stddev/MeanMean)advantage0/biases/summaries/stddev/Square(advantage0/biases/summaries/stddev/Const*
T0*

Tidx0*
	keep_dims( 
a
'advantage0/biases/summaries/stddev/SqrtSqrt'advantage0/biases/summaries/stddev/Mean*
T0

.advantage0/biases/summaries/stddev/stddev/tagsConst*:
value1B/ B)advantage0/biases/summaries/stddev/stddev*
dtype0

)advantage0/biases/summaries/stddev/stddevScalarSummary.advantage0/biases/summaries/stddev/stddev/tags'advantage0/biases/summaries/stddev/Sqrt*
T0
Q
'advantage0/biases/summaries/stddev/RankConst*
value	B :*
dtype0
X
.advantage0/biases/summaries/stddev/range/startConst*
dtype0*
value	B : 
X
.advantage0/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Æ
(advantage0/biases/summaries/stddev/rangeRange.advantage0/biases/summaries/stddev/range/start'advantage0/biases/summaries/stddev/Rank.advantage0/biases/summaries/stddev/range/delta*

Tidx0

&advantage0/biases/summaries/stddev/MaxMaxadvantage0/biases/bias/read(advantage0/biases/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 
~
-advantage0/biases/summaries/stddev/max_1/tagsConst*9
value0B. B(advantage0/biases/summaries/stddev/max_1*
dtype0

(advantage0/biases/summaries/stddev/max_1ScalarSummary-advantage0/biases/summaries/stddev/max_1/tags&advantage0/biases/summaries/stddev/Max*
T0
S
)advantage0/biases/summaries/stddev/Rank_1Const*
value	B :*
dtype0
Z
0advantage0/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
Z
0advantage0/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Î
*advantage0/biases/summaries/stddev/range_1Range0advantage0/biases/summaries/stddev/range_1/start)advantage0/biases/summaries/stddev/Rank_10advantage0/biases/summaries/stddev/range_1/delta*

Tidx0

&advantage0/biases/summaries/stddev/MinMinadvantage0/biases/bias/read*advantage0/biases/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0
~
-advantage0/biases/summaries/stddev/min_1/tagsConst*9
value0B. B(advantage0/biases/summaries/stddev/min_1*
dtype0

(advantage0/biases/summaries/stddev/min_1ScalarSummary-advantage0/biases/summaries/stddev/min_1/tags&advantage0/biases/summaries/stddev/Min*
T0

0advantage0/biases/summaries/stddev/histogram/tagConst*=
value4B2 B,advantage0/biases/summaries/stddev/histogram*
dtype0

,advantage0/biases/summaries/stddev/histogramHistogramSummary0advantage0/biases/summaries/stddev/histogram/tagadvantage0/biases/bias/read*
T0

advantage0/Wx_plus_b/MatMulMatMulfully_connected1/activationadvantage0/weights/weight/read*
transpose_a( *
transpose_b( *
T0
b
advantage0/Wx_plus_b/addAddadvantage0/Wx_plus_b/MatMuladvantage0/biases/bias/read*
T0
u
(advantage0/Wx_plus_b/pre_activations/tagConst*5
value,B* B$advantage0/Wx_plus_b/pre_activations*
dtype0

$advantage0/Wx_plus_b/pre_activationsHistogramSummary(advantage0/Wx_plus_b/pre_activations/tagadvantage0/Wx_plus_b/add*
T0
?
advantage0/activationEluadvantage0/Wx_plus_b/add*
T0
Y
advantage0/activations/tagConst*'
valueB Badvantage0/activations*
dtype0
f
advantage0/activationsHistogramSummaryadvantage0/activations/tagadvantage0/activation*
T0
\
'advantage1/weights/random_uniform/shapeConst*
valueB"      *
dtype0
R
%advantage1/weights/random_uniform/minConst*
valueB
 *  ¿*
dtype0
R
%advantage1/weights/random_uniform/maxConst*
valueB
 *  ?*
dtype0

/advantage1/weights/random_uniform/RandomUniformRandomUniform'advantage1/weights/random_uniform/shape*
dtype0*
seed2 *

seed *
T0

%advantage1/weights/random_uniform/subSub%advantage1/weights/random_uniform/max%advantage1/weights/random_uniform/min*
T0

%advantage1/weights/random_uniform/mulMul/advantage1/weights/random_uniform/RandomUniform%advantage1/weights/random_uniform/sub*
T0

!advantage1/weights/random_uniformAdd%advantage1/weights/random_uniform/mul%advantage1/weights/random_uniform/min*
T0
m
advantage1/weights/weight
VariableV2*
dtype0*
	container *
shape
:*
shared_name 
È
 advantage1/weights/weight/AssignAssignadvantage1/weights/weight!advantage1/weights/random_uniform*
use_locking(*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(
|
advantage1/weights/weight/readIdentityadvantage1/weights/weight*
T0*,
_class"
 loc:@advantage1/weights/weight
K
!advantage1/weights/summaries/RankConst*
value	B :*
dtype0
R
(advantage1/weights/summaries/range/startConst*
value	B : *
dtype0
R
(advantage1/weights/summaries/range/deltaConst*
value	B :*
dtype0
®
"advantage1/weights/summaries/rangeRange(advantage1/weights/summaries/range/start!advantage1/weights/summaries/Rank(advantage1/weights/summaries/range/delta*

Tidx0

!advantage1/weights/summaries/MeanMeanadvantage1/weights/weight/read"advantage1/weights/summaries/range*

Tidx0*
	keep_dims( *
T0
t
(advantage1/weights/summaries/mean_1/tagsConst*4
value+B) B#advantage1/weights/summaries/mean_1*
dtype0

#advantage1/weights/summaries/mean_1ScalarSummary(advantage1/weights/summaries/mean_1/tags!advantage1/weights/summaries/Mean*
T0
z
'advantage1/weights/summaries/stddev/subSubadvantage1/weights/weight/read!advantage1/weights/summaries/Mean*
T0
f
*advantage1/weights/summaries/stddev/SquareSquare'advantage1/weights/summaries/stddev/sub*
T0
^
)advantage1/weights/summaries/stddev/ConstConst*
valueB"       *
dtype0
­
(advantage1/weights/summaries/stddev/MeanMean*advantage1/weights/summaries/stddev/Square)advantage1/weights/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
c
(advantage1/weights/summaries/stddev/SqrtSqrt(advantage1/weights/summaries/stddev/Mean*
T0

/advantage1/weights/summaries/stddev/stddev/tagsConst*;
value2B0 B*advantage1/weights/summaries/stddev/stddev*
dtype0

*advantage1/weights/summaries/stddev/stddevScalarSummary/advantage1/weights/summaries/stddev/stddev/tags(advantage1/weights/summaries/stddev/Sqrt*
T0
R
(advantage1/weights/summaries/stddev/RankConst*
value	B :*
dtype0
Y
/advantage1/weights/summaries/stddev/range/startConst*
value	B : *
dtype0
Y
/advantage1/weights/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Ê
)advantage1/weights/summaries/stddev/rangeRange/advantage1/weights/summaries/stddev/range/start(advantage1/weights/summaries/stddev/Rank/advantage1/weights/summaries/stddev/range/delta*

Tidx0

'advantage1/weights/summaries/stddev/MaxMaxadvantage1/weights/weight/read)advantage1/weights/summaries/stddev/range*

Tidx0*
	keep_dims( *
T0

.advantage1/weights/summaries/stddev/max_1/tagsConst*:
value1B/ B)advantage1/weights/summaries/stddev/max_1*
dtype0

)advantage1/weights/summaries/stddev/max_1ScalarSummary.advantage1/weights/summaries/stddev/max_1/tags'advantage1/weights/summaries/stddev/Max*
T0
T
*advantage1/weights/summaries/stddev/Rank_1Const*
value	B :*
dtype0
[
1advantage1/weights/summaries/stddev/range_1/startConst*
value	B : *
dtype0
[
1advantage1/weights/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Ò
+advantage1/weights/summaries/stddev/range_1Range1advantage1/weights/summaries/stddev/range_1/start*advantage1/weights/summaries/stddev/Rank_11advantage1/weights/summaries/stddev/range_1/delta*

Tidx0
¡
'advantage1/weights/summaries/stddev/MinMinadvantage1/weights/weight/read+advantage1/weights/summaries/stddev/range_1*
T0*

Tidx0*
	keep_dims( 

.advantage1/weights/summaries/stddev/min_1/tagsConst*:
value1B/ B)advantage1/weights/summaries/stddev/min_1*
dtype0

)advantage1/weights/summaries/stddev/min_1ScalarSummary.advantage1/weights/summaries/stddev/min_1/tags'advantage1/weights/summaries/stddev/Min*
T0

1advantage1/weights/summaries/stddev/histogram/tagConst*>
value5B3 B-advantage1/weights/summaries/stddev/histogram*
dtype0

-advantage1/weights/summaries/stddev/histogramHistogramSummary1advantage1/weights/summaries/stddev/histogram/tagadvantage1/weights/weight/read*
T0
T
&advantage1/biases/random_uniform/shapeConst*
valueB:*
dtype0
Q
$advantage1/biases/random_uniform/minConst*
valueB
 *qÄ¿*
dtype0
Q
$advantage1/biases/random_uniform/maxConst*
valueB
 *qÄ?*
dtype0

.advantage1/biases/random_uniform/RandomUniformRandomUniform&advantage1/biases/random_uniform/shape*
dtype0*
seed2 *

seed *
T0

$advantage1/biases/random_uniform/subSub$advantage1/biases/random_uniform/max$advantage1/biases/random_uniform/min*
T0

$advantage1/biases/random_uniform/mulMul.advantage1/biases/random_uniform/RandomUniform$advantage1/biases/random_uniform/sub*
T0
|
 advantage1/biases/random_uniformAdd$advantage1/biases/random_uniform/mul$advantage1/biases/random_uniform/min*
T0
f
advantage1/biases/bias
VariableV2*
shared_name *
dtype0*
	container *
shape:
¾
advantage1/biases/bias/AssignAssignadvantage1/biases/bias advantage1/biases/random_uniform*
use_locking(*
T0*)
_class
loc:@advantage1/biases/bias*
validate_shape(
s
advantage1/biases/bias/readIdentityadvantage1/biases/bias*
T0*)
_class
loc:@advantage1/biases/bias
J
 advantage1/biases/summaries/RankConst*
dtype0*
value	B :
Q
'advantage1/biases/summaries/range/startConst*
value	B : *
dtype0
Q
'advantage1/biases/summaries/range/deltaConst*
value	B :*
dtype0
ª
!advantage1/biases/summaries/rangeRange'advantage1/biases/summaries/range/start advantage1/biases/summaries/Rank'advantage1/biases/summaries/range/delta*

Tidx0

 advantage1/biases/summaries/MeanMeanadvantage1/biases/bias/read!advantage1/biases/summaries/range*

Tidx0*
	keep_dims( *
T0
r
'advantage1/biases/summaries/mean_1/tagsConst*3
value*B( B"advantage1/biases/summaries/mean_1*
dtype0

"advantage1/biases/summaries/mean_1ScalarSummary'advantage1/biases/summaries/mean_1/tags advantage1/biases/summaries/Mean*
T0
u
&advantage1/biases/summaries/stddev/subSubadvantage1/biases/bias/read advantage1/biases/summaries/Mean*
T0
d
)advantage1/biases/summaries/stddev/SquareSquare&advantage1/biases/summaries/stddev/sub*
T0
V
(advantage1/biases/summaries/stddev/ConstConst*
valueB: *
dtype0
ª
'advantage1/biases/summaries/stddev/MeanMean)advantage1/biases/summaries/stddev/Square(advantage1/biases/summaries/stddev/Const*

Tidx0*
	keep_dims( *
T0
a
'advantage1/biases/summaries/stddev/SqrtSqrt'advantage1/biases/summaries/stddev/Mean*
T0

.advantage1/biases/summaries/stddev/stddev/tagsConst*:
value1B/ B)advantage1/biases/summaries/stddev/stddev*
dtype0

)advantage1/biases/summaries/stddev/stddevScalarSummary.advantage1/biases/summaries/stddev/stddev/tags'advantage1/biases/summaries/stddev/Sqrt*
T0
Q
'advantage1/biases/summaries/stddev/RankConst*
value	B :*
dtype0
X
.advantage1/biases/summaries/stddev/range/startConst*
value	B : *
dtype0
X
.advantage1/biases/summaries/stddev/range/deltaConst*
value	B :*
dtype0
Æ
(advantage1/biases/summaries/stddev/rangeRange.advantage1/biases/summaries/stddev/range/start'advantage1/biases/summaries/stddev/Rank.advantage1/biases/summaries/stddev/range/delta*

Tidx0

&advantage1/biases/summaries/stddev/MaxMaxadvantage1/biases/bias/read(advantage1/biases/summaries/stddev/range*
T0*

Tidx0*
	keep_dims( 
~
-advantage1/biases/summaries/stddev/max_1/tagsConst*
dtype0*9
value0B. B(advantage1/biases/summaries/stddev/max_1

(advantage1/biases/summaries/stddev/max_1ScalarSummary-advantage1/biases/summaries/stddev/max_1/tags&advantage1/biases/summaries/stddev/Max*
T0
S
)advantage1/biases/summaries/stddev/Rank_1Const*
value	B :*
dtype0
Z
0advantage1/biases/summaries/stddev/range_1/startConst*
value	B : *
dtype0
Z
0advantage1/biases/summaries/stddev/range_1/deltaConst*
value	B :*
dtype0
Î
*advantage1/biases/summaries/stddev/range_1Range0advantage1/biases/summaries/stddev/range_1/start)advantage1/biases/summaries/stddev/Rank_10advantage1/biases/summaries/stddev/range_1/delta*

Tidx0

&advantage1/biases/summaries/stddev/MinMinadvantage1/biases/bias/read*advantage1/biases/summaries/stddev/range_1*

Tidx0*
	keep_dims( *
T0
~
-advantage1/biases/summaries/stddev/min_1/tagsConst*9
value0B. B(advantage1/biases/summaries/stddev/min_1*
dtype0

(advantage1/biases/summaries/stddev/min_1ScalarSummary-advantage1/biases/summaries/stddev/min_1/tags&advantage1/biases/summaries/stddev/Min*
T0

0advantage1/biases/summaries/stddev/histogram/tagConst*
dtype0*=
value4B2 B,advantage1/biases/summaries/stddev/histogram

,advantage1/biases/summaries/stddev/histogramHistogramSummary0advantage1/biases/summaries/stddev/histogram/tagadvantage1/biases/bias/read*
T0

advantage1/Wx_plus_b/MatMulMatMuladvantage0/activationadvantage1/weights/weight/read*
T0*
transpose_a( *
transpose_b( 
b
advantage1/Wx_plus_b/addAddadvantage1/Wx_plus_b/MatMuladvantage1/biases/bias/read*
T0
u
(advantage1/Wx_plus_b/pre_activations/tagConst*
dtype0*5
value,B* B$advantage1/Wx_plus_b/pre_activations

$advantage1/Wx_plus_b/pre_activationsHistogramSummary(advantage1/Wx_plus_b/pre_activations/tagadvantage1/Wx_plus_b/add*
T0
?
advantage1/activationEluadvantage1/Wx_plus_b/add*
T0
Y
advantage1/activations/tagConst*
dtype0*'
valueB Badvantage1/activations
f
advantage1/activationsHistogramSummaryadvantage1/activations/tagadvantage1/activation*
T0
@
Mean/reduction_indicesConst*
value	B :*
dtype0
a
MeanMeanadvantage1/activationMean/reduction_indices*

Tidx0*
	keep_dims(*
T0
0
SubSubadvantage1/activationMean*
T0
+
addAddvalue1/activationSub*
T0
 
outputIdentityadd*
T0
'
mulMuladdoutput_target*
T0
?
Sum/reduction_indicesConst*
value	B :*
dtype0
L
SumSummulSum/reduction_indices*
T0*

Tidx0*
	keep_dims( 
C
SquaredDifferenceSquaredDifferencescalar_targetSum*
T0
3
ConstConst*
valueB: *
dtype0
L
lossMeanSquaredDifferenceConst*

Tidx0*
	keep_dims( *
T0
8
gradients/ShapeConst*
valueB *
dtype0
@
gradients/grad_ys_0Const*
valueB
 *  ?*
dtype0
W
gradients/FillFillgradients/Shapegradients/grad_ys_0*
T0*

index_type0
O
!gradients/loss_grad/Reshape/shapeConst*
valueB:*
dtype0
p
gradients/loss_grad/ReshapeReshapegradients/Fill!gradients/loss_grad/Reshape/shape*
T0*
Tshape0
N
gradients/loss_grad/ShapeShapeSquaredDifference*
T0*
out_type0
s
gradients/loss_grad/TileTilegradients/loss_grad/Reshapegradients/loss_grad/Shape*

Tmultiples0*
T0
P
gradients/loss_grad/Shape_1ShapeSquaredDifference*
T0*
out_type0
D
gradients/loss_grad/Shape_2Const*
valueB *
dtype0
G
gradients/loss_grad/ConstConst*
valueB: *
dtype0
~
gradients/loss_grad/ProdProdgradients/loss_grad/Shape_1gradients/loss_grad/Const*

Tidx0*
	keep_dims( *
T0
I
gradients/loss_grad/Const_1Const*
valueB: *
dtype0

gradients/loss_grad/Prod_1Prodgradients/loss_grad/Shape_2gradients/loss_grad/Const_1*

Tidx0*
	keep_dims( *
T0
G
gradients/loss_grad/Maximum/yConst*
value	B :*
dtype0
j
gradients/loss_grad/MaximumMaximumgradients/loss_grad/Prod_1gradients/loss_grad/Maximum/y*
T0
h
gradients/loss_grad/floordivFloorDivgradients/loss_grad/Prodgradients/loss_grad/Maximum*
T0
f
gradients/loss_grad/CastCastgradients/loss_grad/floordiv*

SrcT0*
Truncate( *

DstT0
c
gradients/loss_grad/truedivRealDivgradients/loss_grad/Tilegradients/loss_grad/Cast*
T0
W
&gradients/SquaredDifference_grad/ShapeShapescalar_target*
T0*
out_type0
O
(gradients/SquaredDifference_grad/Shape_1ShapeSum*
T0*
out_type0
ª
6gradients/SquaredDifference_grad/BroadcastGradientArgsBroadcastGradientArgs&gradients/SquaredDifference_grad/Shape(gradients/SquaredDifference_grad/Shape_1*
T0
r
'gradients/SquaredDifference_grad/scalarConst^gradients/loss_grad/truediv*
valueB
 *   @*
dtype0
z
$gradients/SquaredDifference_grad/MulMul'gradients/SquaredDifference_grad/scalargradients/loss_grad/truediv*
T0
f
$gradients/SquaredDifference_grad/subSubscalar_targetSum^gradients/loss_grad/truediv*
T0

&gradients/SquaredDifference_grad/mul_1Mul$gradients/SquaredDifference_grad/Mul$gradients/SquaredDifference_grad/sub*
T0
±
$gradients/SquaredDifference_grad/SumSum&gradients/SquaredDifference_grad/mul_16gradients/SquaredDifference_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0

(gradients/SquaredDifference_grad/ReshapeReshape$gradients/SquaredDifference_grad/Sum&gradients/SquaredDifference_grad/Shape*
Tshape0*
T0
µ
&gradients/SquaredDifference_grad/Sum_1Sum&gradients/SquaredDifference_grad/mul_18gradients/SquaredDifference_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0

*gradients/SquaredDifference_grad/Reshape_1Reshape&gradients/SquaredDifference_grad/Sum_1(gradients/SquaredDifference_grad/Shape_1*
T0*
Tshape0
`
$gradients/SquaredDifference_grad/NegNeg*gradients/SquaredDifference_grad/Reshape_1*
T0

1gradients/SquaredDifference_grad/tuple/group_depsNoOp%^gradients/SquaredDifference_grad/Neg)^gradients/SquaredDifference_grad/Reshape
é
9gradients/SquaredDifference_grad/tuple/control_dependencyIdentity(gradients/SquaredDifference_grad/Reshape2^gradients/SquaredDifference_grad/tuple/group_deps*
T0*;
_class1
/-loc:@gradients/SquaredDifference_grad/Reshape
ã
;gradients/SquaredDifference_grad/tuple/control_dependency_1Identity$gradients/SquaredDifference_grad/Neg2^gradients/SquaredDifference_grad/tuple/group_deps*
T0*7
_class-
+)loc:@gradients/SquaredDifference_grad/Neg
?
gradients/Sum_grad/ShapeShapemul*
T0*
out_type0
n
gradients/Sum_grad/SizeConst*+
_class!
loc:@gradients/Sum_grad/Shape*
value	B :*
dtype0

gradients/Sum_grad/addAddSum/reduction_indicesgradients/Sum_grad/Size*
T0*+
_class!
loc:@gradients/Sum_grad/Shape

gradients/Sum_grad/modFloorModgradients/Sum_grad/addgradients/Sum_grad/Size*+
_class!
loc:@gradients/Sum_grad/Shape*
T0
p
gradients/Sum_grad/Shape_1Const*+
_class!
loc:@gradients/Sum_grad/Shape*
valueB *
dtype0
u
gradients/Sum_grad/range/startConst*+
_class!
loc:@gradients/Sum_grad/Shape*
value	B : *
dtype0
u
gradients/Sum_grad/range/deltaConst*+
_class!
loc:@gradients/Sum_grad/Shape*
value	B :*
dtype0
³
gradients/Sum_grad/rangeRangegradients/Sum_grad/range/startgradients/Sum_grad/Sizegradients/Sum_grad/range/delta*

Tidx0*+
_class!
loc:@gradients/Sum_grad/Shape
t
gradients/Sum_grad/Fill/valueConst*+
_class!
loc:@gradients/Sum_grad/Shape*
value	B :*
dtype0
¢
gradients/Sum_grad/FillFillgradients/Sum_grad/Shape_1gradients/Sum_grad/Fill/value*
T0*+
_class!
loc:@gradients/Sum_grad/Shape*

index_type0
Õ
 gradients/Sum_grad/DynamicStitchDynamicStitchgradients/Sum_grad/rangegradients/Sum_grad/modgradients/Sum_grad/Shapegradients/Sum_grad/Fill*
T0*+
_class!
loc:@gradients/Sum_grad/Shape*
N
s
gradients/Sum_grad/Maximum/yConst*+
_class!
loc:@gradients/Sum_grad/Shape*
value	B :*
dtype0

gradients/Sum_grad/MaximumMaximum gradients/Sum_grad/DynamicStitchgradients/Sum_grad/Maximum/y*
T0*+
_class!
loc:@gradients/Sum_grad/Shape

gradients/Sum_grad/floordivFloorDivgradients/Sum_grad/Shapegradients/Sum_grad/Maximum*
T0*+
_class!
loc:@gradients/Sum_grad/Shape

gradients/Sum_grad/ReshapeReshape;gradients/SquaredDifference_grad/tuple/control_dependency_1 gradients/Sum_grad/DynamicStitch*
T0*
Tshape0
s
gradients/Sum_grad/TileTilegradients/Sum_grad/Reshapegradients/Sum_grad/floordiv*

Tmultiples0*
T0
?
gradients/mul_grad/ShapeShapeadd*
T0*
out_type0
K
gradients/mul_grad/Shape_1Shapeoutput_target*
T0*
out_type0

(gradients/mul_grad/BroadcastGradientArgsBroadcastGradientArgsgradients/mul_grad/Shapegradients/mul_grad/Shape_1*
T0
N
gradients/mul_grad/MulMulgradients/Sum_grad/Tileoutput_target*
T0

gradients/mul_grad/SumSumgradients/mul_grad/Mul(gradients/mul_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
n
gradients/mul_grad/ReshapeReshapegradients/mul_grad/Sumgradients/mul_grad/Shape*
T0*
Tshape0
F
gradients/mul_grad/Mul_1Muladdgradients/Sum_grad/Tile*
T0

gradients/mul_grad/Sum_1Sumgradients/mul_grad/Mul_1*gradients/mul_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
t
gradients/mul_grad/Reshape_1Reshapegradients/mul_grad/Sum_1gradients/mul_grad/Shape_1*
T0*
Tshape0
g
#gradients/mul_grad/tuple/group_depsNoOp^gradients/mul_grad/Reshape^gradients/mul_grad/Reshape_1
±
+gradients/mul_grad/tuple/control_dependencyIdentitygradients/mul_grad/Reshape$^gradients/mul_grad/tuple/group_deps*
T0*-
_class#
!loc:@gradients/mul_grad/Reshape
·
-gradients/mul_grad/tuple/control_dependency_1Identitygradients/mul_grad/Reshape_1$^gradients/mul_grad/tuple/group_deps*
T0*/
_class%
#!loc:@gradients/mul_grad/Reshape_1
M
gradients/add_grad/ShapeShapevalue1/activation*
T0*
out_type0
A
gradients/add_grad/Shape_1ShapeSub*
T0*
out_type0

(gradients/add_grad/BroadcastGradientArgsBroadcastGradientArgsgradients/add_grad/Shapegradients/add_grad/Shape_1*
T0

gradients/add_grad/SumSum+gradients/mul_grad/tuple/control_dependency(gradients/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
n
gradients/add_grad/ReshapeReshapegradients/add_grad/Sumgradients/add_grad/Shape*
T0*
Tshape0

gradients/add_grad/Sum_1Sum+gradients/mul_grad/tuple/control_dependency*gradients/add_grad/BroadcastGradientArgs:1*
T0*

Tidx0*
	keep_dims( 
t
gradients/add_grad/Reshape_1Reshapegradients/add_grad/Sum_1gradients/add_grad/Shape_1*
T0*
Tshape0
g
#gradients/add_grad/tuple/group_depsNoOp^gradients/add_grad/Reshape^gradients/add_grad/Reshape_1
±
+gradients/add_grad/tuple/control_dependencyIdentitygradients/add_grad/Reshape$^gradients/add_grad/tuple/group_deps*
T0*-
_class#
!loc:@gradients/add_grad/Reshape
·
-gradients/add_grad/tuple/control_dependency_1Identitygradients/add_grad/Reshape_1$^gradients/add_grad/tuple/group_deps*
T0*/
_class%
#!loc:@gradients/add_grad/Reshape_1
|
(gradients/value1/activation_grad/EluGradEluGrad+gradients/add_grad/tuple/control_dependencyvalue1/activation*
T0
Q
gradients/Sub_grad/ShapeShapeadvantage1/activation*
T0*
out_type0
B
gradients/Sub_grad/Shape_1ShapeMean*
T0*
out_type0

(gradients/Sub_grad/BroadcastGradientArgsBroadcastGradientArgsgradients/Sub_grad/Shapegradients/Sub_grad/Shape_1*
T0

gradients/Sub_grad/SumSum-gradients/add_grad/tuple/control_dependency_1(gradients/Sub_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
n
gradients/Sub_grad/ReshapeReshapegradients/Sub_grad/Sumgradients/Sub_grad/Shape*
T0*
Tshape0
 
gradients/Sub_grad/Sum_1Sum-gradients/add_grad/tuple/control_dependency_1*gradients/Sub_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
@
gradients/Sub_grad/NegNeggradients/Sub_grad/Sum_1*
T0
r
gradients/Sub_grad/Reshape_1Reshapegradients/Sub_grad/Neggradients/Sub_grad/Shape_1*
T0*
Tshape0
g
#gradients/Sub_grad/tuple/group_depsNoOp^gradients/Sub_grad/Reshape^gradients/Sub_grad/Reshape_1
±
+gradients/Sub_grad/tuple/control_dependencyIdentitygradients/Sub_grad/Reshape$^gradients/Sub_grad/tuple/group_deps*
T0*-
_class#
!loc:@gradients/Sub_grad/Reshape
·
-gradients/Sub_grad/tuple/control_dependency_1Identitygradients/Sub_grad/Reshape_1$^gradients/Sub_grad/tuple/group_deps*
T0*/
_class%
#!loc:@gradients/Sub_grad/Reshape_1
d
)gradients/value1/Wx_plus_b/add_grad/ShapeShapevalue1/Wx_plus_b/MatMul*
T0*
out_type0
Y
+gradients/value1/Wx_plus_b/add_grad/Shape_1Const*
valueB:*
dtype0
³
9gradients/value1/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs)gradients/value1/Wx_plus_b/add_grad/Shape+gradients/value1/Wx_plus_b/add_grad/Shape_1*
T0
¹
'gradients/value1/Wx_plus_b/add_grad/SumSum(gradients/value1/activation_grad/EluGrad9gradients/value1/Wx_plus_b/add_grad/BroadcastGradientArgs*
T0*

Tidx0*
	keep_dims( 
¡
+gradients/value1/Wx_plus_b/add_grad/ReshapeReshape'gradients/value1/Wx_plus_b/add_grad/Sum)gradients/value1/Wx_plus_b/add_grad/Shape*
T0*
Tshape0
½
)gradients/value1/Wx_plus_b/add_grad/Sum_1Sum(gradients/value1/activation_grad/EluGrad;gradients/value1/Wx_plus_b/add_grad/BroadcastGradientArgs:1*
T0*

Tidx0*
	keep_dims( 
§
-gradients/value1/Wx_plus_b/add_grad/Reshape_1Reshape)gradients/value1/Wx_plus_b/add_grad/Sum_1+gradients/value1/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0

4gradients/value1/Wx_plus_b/add_grad/tuple/group_depsNoOp,^gradients/value1/Wx_plus_b/add_grad/Reshape.^gradients/value1/Wx_plus_b/add_grad/Reshape_1
õ
<gradients/value1/Wx_plus_b/add_grad/tuple/control_dependencyIdentity+gradients/value1/Wx_plus_b/add_grad/Reshape5^gradients/value1/Wx_plus_b/add_grad/tuple/group_deps*
T0*>
_class4
20loc:@gradients/value1/Wx_plus_b/add_grad/Reshape
û
>gradients/value1/Wx_plus_b/add_grad/tuple/control_dependency_1Identity-gradients/value1/Wx_plus_b/add_grad/Reshape_15^gradients/value1/Wx_plus_b/add_grad/tuple/group_deps*
T0*@
_class6
42loc:@gradients/value1/Wx_plus_b/add_grad/Reshape_1
R
gradients/Mean_grad/ShapeShapeadvantage1/activation*
T0*
out_type0
p
gradients/Mean_grad/SizeConst*,
_class"
 loc:@gradients/Mean_grad/Shape*
value	B :*
dtype0

gradients/Mean_grad/addAddMean/reduction_indicesgradients/Mean_grad/Size*
T0*,
_class"
 loc:@gradients/Mean_grad/Shape

gradients/Mean_grad/modFloorModgradients/Mean_grad/addgradients/Mean_grad/Size*
T0*,
_class"
 loc:@gradients/Mean_grad/Shape
r
gradients/Mean_grad/Shape_1Const*,
_class"
 loc:@gradients/Mean_grad/Shape*
valueB *
dtype0
w
gradients/Mean_grad/range/startConst*,
_class"
 loc:@gradients/Mean_grad/Shape*
value	B : *
dtype0
w
gradients/Mean_grad/range/deltaConst*,
_class"
 loc:@gradients/Mean_grad/Shape*
value	B :*
dtype0
¸
gradients/Mean_grad/rangeRangegradients/Mean_grad/range/startgradients/Mean_grad/Sizegradients/Mean_grad/range/delta*

Tidx0*,
_class"
 loc:@gradients/Mean_grad/Shape
v
gradients/Mean_grad/Fill/valueConst*,
_class"
 loc:@gradients/Mean_grad/Shape*
value	B :*
dtype0
¦
gradients/Mean_grad/FillFillgradients/Mean_grad/Shape_1gradients/Mean_grad/Fill/value*
T0*,
_class"
 loc:@gradients/Mean_grad/Shape*

index_type0
Û
!gradients/Mean_grad/DynamicStitchDynamicStitchgradients/Mean_grad/rangegradients/Mean_grad/modgradients/Mean_grad/Shapegradients/Mean_grad/Fill*
N*
T0*,
_class"
 loc:@gradients/Mean_grad/Shape
u
gradients/Mean_grad/Maximum/yConst*,
_class"
 loc:@gradients/Mean_grad/Shape*
value	B :*
dtype0

gradients/Mean_grad/MaximumMaximum!gradients/Mean_grad/DynamicStitchgradients/Mean_grad/Maximum/y*
T0*,
_class"
 loc:@gradients/Mean_grad/Shape

gradients/Mean_grad/floordivFloorDivgradients/Mean_grad/Shapegradients/Mean_grad/Maximum*,
_class"
 loc:@gradients/Mean_grad/Shape*
T0

gradients/Mean_grad/ReshapeReshape-gradients/Sub_grad/tuple/control_dependency_1!gradients/Mean_grad/DynamicStitch*
T0*
Tshape0
v
gradients/Mean_grad/TileTilegradients/Mean_grad/Reshapegradients/Mean_grad/floordiv*

Tmultiples0*
T0
T
gradients/Mean_grad/Shape_2Shapeadvantage1/activation*
T0*
out_type0
C
gradients/Mean_grad/Shape_3ShapeMean*
T0*
out_type0
G
gradients/Mean_grad/ConstConst*
valueB: *
dtype0
~
gradients/Mean_grad/ProdProdgradients/Mean_grad/Shape_2gradients/Mean_grad/Const*

Tidx0*
	keep_dims( *
T0
I
gradients/Mean_grad/Const_1Const*
valueB: *
dtype0

gradients/Mean_grad/Prod_1Prodgradients/Mean_grad/Shape_3gradients/Mean_grad/Const_1*

Tidx0*
	keep_dims( *
T0
I
gradients/Mean_grad/Maximum_1/yConst*
value	B :*
dtype0
n
gradients/Mean_grad/Maximum_1Maximumgradients/Mean_grad/Prod_1gradients/Mean_grad/Maximum_1/y*
T0
l
gradients/Mean_grad/floordiv_1FloorDivgradients/Mean_grad/Prodgradients/Mean_grad/Maximum_1*
T0
h
gradients/Mean_grad/CastCastgradients/Mean_grad/floordiv_1*

SrcT0*
Truncate( *

DstT0
c
gradients/Mean_grad/truedivRealDivgradients/Mean_grad/Tilegradients/Mean_grad/Cast*
T0
À
-gradients/value1/Wx_plus_b/MatMul_grad/MatMulMatMul<gradients/value1/Wx_plus_b/add_grad/tuple/control_dependencyvalue1/weights/weight/read*
transpose_a( *
transpose_b(*
T0
¹
/gradients/value1/Wx_plus_b/MatMul_grad/MatMul_1MatMulvalue0/activation<gradients/value1/Wx_plus_b/add_grad/tuple/control_dependency*
transpose_b( *
T0*
transpose_a(
¡
7gradients/value1/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp.^gradients/value1/Wx_plus_b/MatMul_grad/MatMul0^gradients/value1/Wx_plus_b/MatMul_grad/MatMul_1
ÿ
?gradients/value1/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity-gradients/value1/Wx_plus_b/MatMul_grad/MatMul8^gradients/value1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*@
_class6
42loc:@gradients/value1/Wx_plus_b/MatMul_grad/MatMul

Agradients/value1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity/gradients/value1/Wx_plus_b/MatMul_grad/MatMul_18^gradients/value1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*B
_class8
64loc:@gradients/value1/Wx_plus_b/MatMul_grad/MatMul_1
¡
gradients/AddNAddN+gradients/Sub_grad/tuple/control_dependencygradients/Mean_grad/truediv*-
_class#
!loc:@gradients/Sub_grad/Reshape*
N*
T0
g
,gradients/advantage1/activation_grad/EluGradEluGradgradients/AddNadvantage1/activation*
T0

(gradients/value0/activation_grad/EluGradEluGrad?gradients/value1/Wx_plus_b/MatMul_grad/tuple/control_dependencyvalue0/activation*
T0
l
-gradients/advantage1/Wx_plus_b/add_grad/ShapeShapeadvantage1/Wx_plus_b/MatMul*
T0*
out_type0
]
/gradients/advantage1/Wx_plus_b/add_grad/Shape_1Const*
valueB:*
dtype0
¿
=gradients/advantage1/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs-gradients/advantage1/Wx_plus_b/add_grad/Shape/gradients/advantage1/Wx_plus_b/add_grad/Shape_1*
T0
Å
+gradients/advantage1/Wx_plus_b/add_grad/SumSum,gradients/advantage1/activation_grad/EluGrad=gradients/advantage1/Wx_plus_b/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
­
/gradients/advantage1/Wx_plus_b/add_grad/ReshapeReshape+gradients/advantage1/Wx_plus_b/add_grad/Sum-gradients/advantage1/Wx_plus_b/add_grad/Shape*
T0*
Tshape0
É
-gradients/advantage1/Wx_plus_b/add_grad/Sum_1Sum,gradients/advantage1/activation_grad/EluGrad?gradients/advantage1/Wx_plus_b/add_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
³
1gradients/advantage1/Wx_plus_b/add_grad/Reshape_1Reshape-gradients/advantage1/Wx_plus_b/add_grad/Sum_1/gradients/advantage1/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0
¦
8gradients/advantage1/Wx_plus_b/add_grad/tuple/group_depsNoOp0^gradients/advantage1/Wx_plus_b/add_grad/Reshape2^gradients/advantage1/Wx_plus_b/add_grad/Reshape_1

@gradients/advantage1/Wx_plus_b/add_grad/tuple/control_dependencyIdentity/gradients/advantage1/Wx_plus_b/add_grad/Reshape9^gradients/advantage1/Wx_plus_b/add_grad/tuple/group_deps*B
_class8
64loc:@gradients/advantage1/Wx_plus_b/add_grad/Reshape*
T0

Bgradients/advantage1/Wx_plus_b/add_grad/tuple/control_dependency_1Identity1gradients/advantage1/Wx_plus_b/add_grad/Reshape_19^gradients/advantage1/Wx_plus_b/add_grad/tuple/group_deps*
T0*D
_class:
86loc:@gradients/advantage1/Wx_plus_b/add_grad/Reshape_1
d
)gradients/value0/Wx_plus_b/add_grad/ShapeShapevalue0/Wx_plus_b/MatMul*
T0*
out_type0
Y
+gradients/value0/Wx_plus_b/add_grad/Shape_1Const*
dtype0*
valueB:
³
9gradients/value0/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs)gradients/value0/Wx_plus_b/add_grad/Shape+gradients/value0/Wx_plus_b/add_grad/Shape_1*
T0
¹
'gradients/value0/Wx_plus_b/add_grad/SumSum(gradients/value0/activation_grad/EluGrad9gradients/value0/Wx_plus_b/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
¡
+gradients/value0/Wx_plus_b/add_grad/ReshapeReshape'gradients/value0/Wx_plus_b/add_grad/Sum)gradients/value0/Wx_plus_b/add_grad/Shape*
T0*
Tshape0
½
)gradients/value0/Wx_plus_b/add_grad/Sum_1Sum(gradients/value0/activation_grad/EluGrad;gradients/value0/Wx_plus_b/add_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
§
-gradients/value0/Wx_plus_b/add_grad/Reshape_1Reshape)gradients/value0/Wx_plus_b/add_grad/Sum_1+gradients/value0/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0

4gradients/value0/Wx_plus_b/add_grad/tuple/group_depsNoOp,^gradients/value0/Wx_plus_b/add_grad/Reshape.^gradients/value0/Wx_plus_b/add_grad/Reshape_1
õ
<gradients/value0/Wx_plus_b/add_grad/tuple/control_dependencyIdentity+gradients/value0/Wx_plus_b/add_grad/Reshape5^gradients/value0/Wx_plus_b/add_grad/tuple/group_deps*
T0*>
_class4
20loc:@gradients/value0/Wx_plus_b/add_grad/Reshape
û
>gradients/value0/Wx_plus_b/add_grad/tuple/control_dependency_1Identity-gradients/value0/Wx_plus_b/add_grad/Reshape_15^gradients/value0/Wx_plus_b/add_grad/tuple/group_deps*
T0*@
_class6
42loc:@gradients/value0/Wx_plus_b/add_grad/Reshape_1
Ì
1gradients/advantage1/Wx_plus_b/MatMul_grad/MatMulMatMul@gradients/advantage1/Wx_plus_b/add_grad/tuple/control_dependencyadvantage1/weights/weight/read*
transpose_a( *
transpose_b(*
T0
Å
3gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul_1MatMuladvantage0/activation@gradients/advantage1/Wx_plus_b/add_grad/tuple/control_dependency*
transpose_a(*
transpose_b( *
T0
­
;gradients/advantage1/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp2^gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul4^gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul_1

Cgradients/advantage1/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity1gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul<^gradients/advantage1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*D
_class:
86loc:@gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul

Egradients/advantage1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity3gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul_1<^gradients/advantage1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*F
_class<
:8loc:@gradients/advantage1/Wx_plus_b/MatMul_grad/MatMul_1
À
-gradients/value0/Wx_plus_b/MatMul_grad/MatMulMatMul<gradients/value0/Wx_plus_b/add_grad/tuple/control_dependencyvalue0/weights/weight/read*
transpose_b(*
T0*
transpose_a( 
Ã
/gradients/value0/Wx_plus_b/MatMul_grad/MatMul_1MatMulfully_connected1/activation<gradients/value0/Wx_plus_b/add_grad/tuple/control_dependency*
transpose_a(*
transpose_b( *
T0
¡
7gradients/value0/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp.^gradients/value0/Wx_plus_b/MatMul_grad/MatMul0^gradients/value0/Wx_plus_b/MatMul_grad/MatMul_1
ÿ
?gradients/value0/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity-gradients/value0/Wx_plus_b/MatMul_grad/MatMul8^gradients/value0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*@
_class6
42loc:@gradients/value0/Wx_plus_b/MatMul_grad/MatMul

Agradients/value0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity/gradients/value0/Wx_plus_b/MatMul_grad/MatMul_18^gradients/value0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*B
_class8
64loc:@gradients/value0/Wx_plus_b/MatMul_grad/MatMul_1

,gradients/advantage0/activation_grad/EluGradEluGradCgradients/advantage1/Wx_plus_b/MatMul_grad/tuple/control_dependencyadvantage0/activation*
T0
l
-gradients/advantage0/Wx_plus_b/add_grad/ShapeShapeadvantage0/Wx_plus_b/MatMul*
T0*
out_type0
]
/gradients/advantage0/Wx_plus_b/add_grad/Shape_1Const*
valueB:*
dtype0
¿
=gradients/advantage0/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs-gradients/advantage0/Wx_plus_b/add_grad/Shape/gradients/advantage0/Wx_plus_b/add_grad/Shape_1*
T0
Å
+gradients/advantage0/Wx_plus_b/add_grad/SumSum,gradients/advantage0/activation_grad/EluGrad=gradients/advantage0/Wx_plus_b/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
­
/gradients/advantage0/Wx_plus_b/add_grad/ReshapeReshape+gradients/advantage0/Wx_plus_b/add_grad/Sum-gradients/advantage0/Wx_plus_b/add_grad/Shape*
T0*
Tshape0
É
-gradients/advantage0/Wx_plus_b/add_grad/Sum_1Sum,gradients/advantage0/activation_grad/EluGrad?gradients/advantage0/Wx_plus_b/add_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
³
1gradients/advantage0/Wx_plus_b/add_grad/Reshape_1Reshape-gradients/advantage0/Wx_plus_b/add_grad/Sum_1/gradients/advantage0/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0
¦
8gradients/advantage0/Wx_plus_b/add_grad/tuple/group_depsNoOp0^gradients/advantage0/Wx_plus_b/add_grad/Reshape2^gradients/advantage0/Wx_plus_b/add_grad/Reshape_1

@gradients/advantage0/Wx_plus_b/add_grad/tuple/control_dependencyIdentity/gradients/advantage0/Wx_plus_b/add_grad/Reshape9^gradients/advantage0/Wx_plus_b/add_grad/tuple/group_deps*B
_class8
64loc:@gradients/advantage0/Wx_plus_b/add_grad/Reshape*
T0

Bgradients/advantage0/Wx_plus_b/add_grad/tuple/control_dependency_1Identity1gradients/advantage0/Wx_plus_b/add_grad/Reshape_19^gradients/advantage0/Wx_plus_b/add_grad/tuple/group_deps*
T0*D
_class:
86loc:@gradients/advantage0/Wx_plus_b/add_grad/Reshape_1
Ì
1gradients/advantage0/Wx_plus_b/MatMul_grad/MatMulMatMul@gradients/advantage0/Wx_plus_b/add_grad/tuple/control_dependencyadvantage0/weights/weight/read*
T0*
transpose_a( *
transpose_b(
Ë
3gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul_1MatMulfully_connected1/activation@gradients/advantage0/Wx_plus_b/add_grad/tuple/control_dependency*
T0*
transpose_a(*
transpose_b( 
­
;gradients/advantage0/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp2^gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul4^gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul_1

Cgradients/advantage0/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity1gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul<^gradients/advantage0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*D
_class:
86loc:@gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul

Egradients/advantage0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity3gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul_1<^gradients/advantage0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*F
_class<
:8loc:@gradients/advantage0/Wx_plus_b/MatMul_grad/MatMul_1
ò
gradients/AddN_1AddN?gradients/value0/Wx_plus_b/MatMul_grad/tuple/control_dependencyCgradients/advantage0/Wx_plus_b/MatMul_grad/tuple/control_dependency*
N*
T0*@
_class6
42loc:@gradients/value0/Wx_plus_b/MatMul_grad/MatMul
u
2gradients/fully_connected1/activation_grad/EluGradEluGradgradients/AddN_1fully_connected1/activation*
T0
x
3gradients/fully_connected1/Wx_plus_b/add_grad/ShapeShape!fully_connected1/Wx_plus_b/MatMul*
T0*
out_type0
c
5gradients/fully_connected1/Wx_plus_b/add_grad/Shape_1Const*
dtype0*
valueB:
Ñ
Cgradients/fully_connected1/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs3gradients/fully_connected1/Wx_plus_b/add_grad/Shape5gradients/fully_connected1/Wx_plus_b/add_grad/Shape_1*
T0
×
1gradients/fully_connected1/Wx_plus_b/add_grad/SumSum2gradients/fully_connected1/activation_grad/EluGradCgradients/fully_connected1/Wx_plus_b/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
¿
5gradients/fully_connected1/Wx_plus_b/add_grad/ReshapeReshape1gradients/fully_connected1/Wx_plus_b/add_grad/Sum3gradients/fully_connected1/Wx_plus_b/add_grad/Shape*
T0*
Tshape0
Û
3gradients/fully_connected1/Wx_plus_b/add_grad/Sum_1Sum2gradients/fully_connected1/activation_grad/EluGradEgradients/fully_connected1/Wx_plus_b/add_grad/BroadcastGradientArgs:1*

Tidx0*
	keep_dims( *
T0
Å
7gradients/fully_connected1/Wx_plus_b/add_grad/Reshape_1Reshape3gradients/fully_connected1/Wx_plus_b/add_grad/Sum_15gradients/fully_connected1/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0
¸
>gradients/fully_connected1/Wx_plus_b/add_grad/tuple/group_depsNoOp6^gradients/fully_connected1/Wx_plus_b/add_grad/Reshape8^gradients/fully_connected1/Wx_plus_b/add_grad/Reshape_1

Fgradients/fully_connected1/Wx_plus_b/add_grad/tuple/control_dependencyIdentity5gradients/fully_connected1/Wx_plus_b/add_grad/Reshape?^gradients/fully_connected1/Wx_plus_b/add_grad/tuple/group_deps*
T0*H
_class>
<:loc:@gradients/fully_connected1/Wx_plus_b/add_grad/Reshape
£
Hgradients/fully_connected1/Wx_plus_b/add_grad/tuple/control_dependency_1Identity7gradients/fully_connected1/Wx_plus_b/add_grad/Reshape_1?^gradients/fully_connected1/Wx_plus_b/add_grad/tuple/group_deps*
T0*J
_class@
><loc:@gradients/fully_connected1/Wx_plus_b/add_grad/Reshape_1
Þ
7gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMulMatMulFgradients/fully_connected1/Wx_plus_b/add_grad/tuple/control_dependency$fully_connected1/weights/weight/read*
T0*
transpose_a( *
transpose_b(
×
9gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul_1MatMulfully_connected0/activationFgradients/fully_connected1/Wx_plus_b/add_grad/tuple/control_dependency*
transpose_a(*
transpose_b( *
T0
¿
Agradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp8^gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul:^gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul_1
§
Igradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity7gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMulB^gradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*J
_class@
><loc:@gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul
­
Kgradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity9gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul_1B^gradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*L
_classB
@>loc:@gradients/fully_connected1/Wx_plus_b/MatMul_grad/MatMul_1
®
2gradients/fully_connected0/activation_grad/EluGradEluGradIgradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/control_dependencyfully_connected0/activation*
T0
x
3gradients/fully_connected0/Wx_plus_b/add_grad/ShapeShape!fully_connected0/Wx_plus_b/MatMul*
T0*
out_type0
c
5gradients/fully_connected0/Wx_plus_b/add_grad/Shape_1Const*
valueB:*
dtype0
Ñ
Cgradients/fully_connected0/Wx_plus_b/add_grad/BroadcastGradientArgsBroadcastGradientArgs3gradients/fully_connected0/Wx_plus_b/add_grad/Shape5gradients/fully_connected0/Wx_plus_b/add_grad/Shape_1*
T0
×
1gradients/fully_connected0/Wx_plus_b/add_grad/SumSum2gradients/fully_connected0/activation_grad/EluGradCgradients/fully_connected0/Wx_plus_b/add_grad/BroadcastGradientArgs*

Tidx0*
	keep_dims( *
T0
¿
5gradients/fully_connected0/Wx_plus_b/add_grad/ReshapeReshape1gradients/fully_connected0/Wx_plus_b/add_grad/Sum3gradients/fully_connected0/Wx_plus_b/add_grad/Shape*
Tshape0*
T0
Û
3gradients/fully_connected0/Wx_plus_b/add_grad/Sum_1Sum2gradients/fully_connected0/activation_grad/EluGradEgradients/fully_connected0/Wx_plus_b/add_grad/BroadcastGradientArgs:1*
T0*

Tidx0*
	keep_dims( 
Å
7gradients/fully_connected0/Wx_plus_b/add_grad/Reshape_1Reshape3gradients/fully_connected0/Wx_plus_b/add_grad/Sum_15gradients/fully_connected0/Wx_plus_b/add_grad/Shape_1*
T0*
Tshape0
¸
>gradients/fully_connected0/Wx_plus_b/add_grad/tuple/group_depsNoOp6^gradients/fully_connected0/Wx_plus_b/add_grad/Reshape8^gradients/fully_connected0/Wx_plus_b/add_grad/Reshape_1

Fgradients/fully_connected0/Wx_plus_b/add_grad/tuple/control_dependencyIdentity5gradients/fully_connected0/Wx_plus_b/add_grad/Reshape?^gradients/fully_connected0/Wx_plus_b/add_grad/tuple/group_deps*
T0*H
_class>
<:loc:@gradients/fully_connected0/Wx_plus_b/add_grad/Reshape
£
Hgradients/fully_connected0/Wx_plus_b/add_grad/tuple/control_dependency_1Identity7gradients/fully_connected0/Wx_plus_b/add_grad/Reshape_1?^gradients/fully_connected0/Wx_plus_b/add_grad/tuple/group_deps*
T0*J
_class@
><loc:@gradients/fully_connected0/Wx_plus_b/add_grad/Reshape_1
Þ
7gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMulMatMulFgradients/fully_connected0/Wx_plus_b/add_grad/tuple/control_dependency$fully_connected0/weights/weight/read*
transpose_b(*
T0*
transpose_a( 
Á
9gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul_1MatMulinputFgradients/fully_connected0/Wx_plus_b/add_grad/tuple/control_dependency*
T0*
transpose_a(*
transpose_b( 
¿
Agradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/group_depsNoOp8^gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul:^gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul_1
§
Igradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/control_dependencyIdentity7gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMulB^gradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*J
_class@
><loc:@gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul
­
Kgradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1Identity9gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul_1B^gradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/group_deps*
T0*L
_classB
@>loc:@gradients/fully_connected0/Wx_plus_b/MatMul_grad/MatMul_1
q
beta1_power/initial_valueConst*)
_class
loc:@advantage0/biases/bias*
valueB
 *fff?*
dtype0

beta1_power
VariableV2*)
_class
loc:@advantage0/biases/bias*
dtype0*
	container *
shape: *
shared_name 
¡
beta1_power/AssignAssignbeta1_powerbeta1_power/initial_value*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(
]
beta1_power/readIdentitybeta1_power*
T0*)
_class
loc:@advantage0/biases/bias
q
beta2_power/initial_valueConst*)
_class
loc:@advantage0/biases/bias*
valueB
 *w¾?*
dtype0

beta2_power
VariableV2*
shared_name *)
_class
loc:@advantage0/biases/bias*
dtype0*
	container *
shape: 
¡
beta2_power/AssignAssignbeta2_powerbeta2_power/initial_value*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(
]
beta2_power/readIdentitybeta2_power*
T0*)
_class
loc:@advantage0/biases/bias

6fully_connected0/weights/weight/Adam/Initializer/zerosConst*
valueB*    *2
_class(
&$loc:@fully_connected0/weights/weight*
dtype0
¬
$fully_connected0/weights/weight/Adam
VariableV2*2
_class(
&$loc:@fully_connected0/weights/weight*
dtype0*
	container *
shape
:*
shared_name 
ù
+fully_connected0/weights/weight/Adam/AssignAssign$fully_connected0/weights/weight/Adam6fully_connected0/weights/weight/Adam/Initializer/zeros*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(

)fully_connected0/weights/weight/Adam/readIdentity$fully_connected0/weights/weight/Adam*
T0*2
_class(
&$loc:@fully_connected0/weights/weight
¡
8fully_connected0/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *2
_class(
&$loc:@fully_connected0/weights/weight*
dtype0
®
&fully_connected0/weights/weight/Adam_1
VariableV2*
shape
:*
shared_name *2
_class(
&$loc:@fully_connected0/weights/weight*
dtype0*
	container 
ÿ
-fully_connected0/weights/weight/Adam_1/AssignAssign&fully_connected0/weights/weight/Adam_18fully_connected0/weights/weight/Adam_1/Initializer/zeros*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(

+fully_connected0/weights/weight/Adam_1/readIdentity&fully_connected0/weights/weight/Adam_1*
T0*2
_class(
&$loc:@fully_connected0/weights/weight

3fully_connected0/biases/bias/Adam/Initializer/zerosConst*
valueB*    */
_class%
#!loc:@fully_connected0/biases/bias*
dtype0
¢
!fully_connected0/biases/bias/Adam
VariableV2*
shared_name */
_class%
#!loc:@fully_connected0/biases/bias*
dtype0*
	container *
shape:
í
(fully_connected0/biases/bias/Adam/AssignAssign!fully_connected0/biases/bias/Adam3fully_connected0/biases/bias/Adam/Initializer/zeros*
validate_shape(*
use_locking(*
T0*/
_class%
#!loc:@fully_connected0/biases/bias

&fully_connected0/biases/bias/Adam/readIdentity!fully_connected0/biases/bias/Adam*
T0*/
_class%
#!loc:@fully_connected0/biases/bias

5fully_connected0/biases/bias/Adam_1/Initializer/zerosConst*
valueB*    */
_class%
#!loc:@fully_connected0/biases/bias*
dtype0
¤
#fully_connected0/biases/bias/Adam_1
VariableV2*
shape:*
shared_name */
_class%
#!loc:@fully_connected0/biases/bias*
dtype0*
	container 
ó
*fully_connected0/biases/bias/Adam_1/AssignAssign#fully_connected0/biases/bias/Adam_15fully_connected0/biases/bias/Adam_1/Initializer/zeros*
validate_shape(*
use_locking(*
T0*/
_class%
#!loc:@fully_connected0/biases/bias

(fully_connected0/biases/bias/Adam_1/readIdentity#fully_connected0/biases/bias/Adam_1*
T0*/
_class%
#!loc:@fully_connected0/biases/bias

6fully_connected1/weights/weight/Adam/Initializer/zerosConst*
valueB*    *2
_class(
&$loc:@fully_connected1/weights/weight*
dtype0
¬
$fully_connected1/weights/weight/Adam
VariableV2*
dtype0*
	container *
shape
:*
shared_name *2
_class(
&$loc:@fully_connected1/weights/weight
ù
+fully_connected1/weights/weight/Adam/AssignAssign$fully_connected1/weights/weight/Adam6fully_connected1/weights/weight/Adam/Initializer/zeros*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight*
validate_shape(

)fully_connected1/weights/weight/Adam/readIdentity$fully_connected1/weights/weight/Adam*2
_class(
&$loc:@fully_connected1/weights/weight*
T0
¡
8fully_connected1/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *2
_class(
&$loc:@fully_connected1/weights/weight*
dtype0
®
&fully_connected1/weights/weight/Adam_1
VariableV2*
shape
:*
shared_name *2
_class(
&$loc:@fully_connected1/weights/weight*
dtype0*
	container 
ÿ
-fully_connected1/weights/weight/Adam_1/AssignAssign&fully_connected1/weights/weight/Adam_18fully_connected1/weights/weight/Adam_1/Initializer/zeros*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight*
validate_shape(

+fully_connected1/weights/weight/Adam_1/readIdentity&fully_connected1/weights/weight/Adam_1*
T0*2
_class(
&$loc:@fully_connected1/weights/weight

3fully_connected1/biases/bias/Adam/Initializer/zerosConst*
dtype0*
valueB*    */
_class%
#!loc:@fully_connected1/biases/bias
¢
!fully_connected1/biases/bias/Adam
VariableV2*
	container *
shape:*
shared_name */
_class%
#!loc:@fully_connected1/biases/bias*
dtype0
í
(fully_connected1/biases/bias/Adam/AssignAssign!fully_connected1/biases/bias/Adam3fully_connected1/biases/bias/Adam/Initializer/zeros*
use_locking(*
T0*/
_class%
#!loc:@fully_connected1/biases/bias*
validate_shape(

&fully_connected1/biases/bias/Adam/readIdentity!fully_connected1/biases/bias/Adam*
T0*/
_class%
#!loc:@fully_connected1/biases/bias

5fully_connected1/biases/bias/Adam_1/Initializer/zerosConst*
dtype0*
valueB*    */
_class%
#!loc:@fully_connected1/biases/bias
¤
#fully_connected1/biases/bias/Adam_1
VariableV2*
shape:*
shared_name */
_class%
#!loc:@fully_connected1/biases/bias*
dtype0*
	container 
ó
*fully_connected1/biases/bias/Adam_1/AssignAssign#fully_connected1/biases/bias/Adam_15fully_connected1/biases/bias/Adam_1/Initializer/zeros*
validate_shape(*
use_locking(*
T0*/
_class%
#!loc:@fully_connected1/biases/bias

(fully_connected1/biases/bias/Adam_1/readIdentity#fully_connected1/biases/bias/Adam_1*
T0*/
_class%
#!loc:@fully_connected1/biases/bias

,value0/weights/weight/Adam/Initializer/zerosConst*
valueB*    *(
_class
loc:@value0/weights/weight*
dtype0

value0/weights/weight/Adam
VariableV2*(
_class
loc:@value0/weights/weight*
dtype0*
	container *
shape
:*
shared_name 
Ñ
!value0/weights/weight/Adam/AssignAssignvalue0/weights/weight/Adam,value0/weights/weight/Adam/Initializer/zeros*
use_locking(*
T0*(
_class
loc:@value0/weights/weight*
validate_shape(
z
value0/weights/weight/Adam/readIdentityvalue0/weights/weight/Adam*
T0*(
_class
loc:@value0/weights/weight

.value0/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *(
_class
loc:@value0/weights/weight*
dtype0

value0/weights/weight/Adam_1
VariableV2*
shape
:*
shared_name *(
_class
loc:@value0/weights/weight*
dtype0*
	container 
×
#value0/weights/weight/Adam_1/AssignAssignvalue0/weights/weight/Adam_1.value0/weights/weight/Adam_1/Initializer/zeros*
validate_shape(*
use_locking(*
T0*(
_class
loc:@value0/weights/weight
~
!value0/weights/weight/Adam_1/readIdentityvalue0/weights/weight/Adam_1*
T0*(
_class
loc:@value0/weights/weight

)value0/biases/bias/Adam/Initializer/zerosConst*
valueB*    *%
_class
loc:@value0/biases/bias*
dtype0

value0/biases/bias/Adam
VariableV2*
shared_name *%
_class
loc:@value0/biases/bias*
dtype0*
	container *
shape:
Å
value0/biases/bias/Adam/AssignAssignvalue0/biases/bias/Adam)value0/biases/bias/Adam/Initializer/zeros*
T0*%
_class
loc:@value0/biases/bias*
validate_shape(*
use_locking(
q
value0/biases/bias/Adam/readIdentityvalue0/biases/bias/Adam*
T0*%
_class
loc:@value0/biases/bias

+value0/biases/bias/Adam_1/Initializer/zerosConst*
valueB*    *%
_class
loc:@value0/biases/bias*
dtype0

value0/biases/bias/Adam_1
VariableV2*
shared_name *%
_class
loc:@value0/biases/bias*
dtype0*
	container *
shape:
Ë
 value0/biases/bias/Adam_1/AssignAssignvalue0/biases/bias/Adam_1+value0/biases/bias/Adam_1/Initializer/zeros*
use_locking(*
T0*%
_class
loc:@value0/biases/bias*
validate_shape(
u
value0/biases/bias/Adam_1/readIdentityvalue0/biases/bias/Adam_1*
T0*%
_class
loc:@value0/biases/bias

,value1/weights/weight/Adam/Initializer/zerosConst*
valueB*    *(
_class
loc:@value1/weights/weight*
dtype0

value1/weights/weight/Adam
VariableV2*
shared_name *(
_class
loc:@value1/weights/weight*
dtype0*
	container *
shape
:
Ñ
!value1/weights/weight/Adam/AssignAssignvalue1/weights/weight/Adam,value1/weights/weight/Adam/Initializer/zeros*
validate_shape(*
use_locking(*
T0*(
_class
loc:@value1/weights/weight
z
value1/weights/weight/Adam/readIdentityvalue1/weights/weight/Adam*
T0*(
_class
loc:@value1/weights/weight

.value1/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *(
_class
loc:@value1/weights/weight*
dtype0

value1/weights/weight/Adam_1
VariableV2*
dtype0*
	container *
shape
:*
shared_name *(
_class
loc:@value1/weights/weight
×
#value1/weights/weight/Adam_1/AssignAssignvalue1/weights/weight/Adam_1.value1/weights/weight/Adam_1/Initializer/zeros*
use_locking(*
T0*(
_class
loc:@value1/weights/weight*
validate_shape(
~
!value1/weights/weight/Adam_1/readIdentityvalue1/weights/weight/Adam_1*
T0*(
_class
loc:@value1/weights/weight

)value1/biases/bias/Adam/Initializer/zerosConst*
valueB*    *%
_class
loc:@value1/biases/bias*
dtype0

value1/biases/bias/Adam
VariableV2*
shape:*
shared_name *%
_class
loc:@value1/biases/bias*
dtype0*
	container 
Å
value1/biases/bias/Adam/AssignAssignvalue1/biases/bias/Adam)value1/biases/bias/Adam/Initializer/zeros*%
_class
loc:@value1/biases/bias*
validate_shape(*
use_locking(*
T0
q
value1/biases/bias/Adam/readIdentityvalue1/biases/bias/Adam*
T0*%
_class
loc:@value1/biases/bias

+value1/biases/bias/Adam_1/Initializer/zerosConst*
valueB*    *%
_class
loc:@value1/biases/bias*
dtype0

value1/biases/bias/Adam_1
VariableV2*
shape:*
shared_name *%
_class
loc:@value1/biases/bias*
dtype0*
	container 
Ë
 value1/biases/bias/Adam_1/AssignAssignvalue1/biases/bias/Adam_1+value1/biases/bias/Adam_1/Initializer/zeros*
T0*%
_class
loc:@value1/biases/bias*
validate_shape(*
use_locking(
u
value1/biases/bias/Adam_1/readIdentityvalue1/biases/bias/Adam_1*
T0*%
_class
loc:@value1/biases/bias

0advantage0/weights/weight/Adam/Initializer/zerosConst*
valueB*    *,
_class"
 loc:@advantage0/weights/weight*
dtype0
 
advantage0/weights/weight/Adam
VariableV2*
shared_name *,
_class"
 loc:@advantage0/weights/weight*
dtype0*
	container *
shape
:
á
%advantage0/weights/weight/Adam/AssignAssignadvantage0/weights/weight/Adam0advantage0/weights/weight/Adam/Initializer/zeros*
use_locking(*
T0*,
_class"
 loc:@advantage0/weights/weight*
validate_shape(

#advantage0/weights/weight/Adam/readIdentityadvantage0/weights/weight/Adam*
T0*,
_class"
 loc:@advantage0/weights/weight

2advantage0/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *,
_class"
 loc:@advantage0/weights/weight*
dtype0
¢
 advantage0/weights/weight/Adam_1
VariableV2*,
_class"
 loc:@advantage0/weights/weight*
dtype0*
	container *
shape
:*
shared_name 
ç
'advantage0/weights/weight/Adam_1/AssignAssign advantage0/weights/weight/Adam_12advantage0/weights/weight/Adam_1/Initializer/zeros*
validate_shape(*
use_locking(*
T0*,
_class"
 loc:@advantage0/weights/weight

%advantage0/weights/weight/Adam_1/readIdentity advantage0/weights/weight/Adam_1*
T0*,
_class"
 loc:@advantage0/weights/weight

-advantage0/biases/bias/Adam/Initializer/zerosConst*
valueB*    *)
_class
loc:@advantage0/biases/bias*
dtype0

advantage0/biases/bias/Adam
VariableV2*
shared_name *)
_class
loc:@advantage0/biases/bias*
dtype0*
	container *
shape:
Õ
"advantage0/biases/bias/Adam/AssignAssignadvantage0/biases/bias/Adam-advantage0/biases/bias/Adam/Initializer/zeros*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(
}
 advantage0/biases/bias/Adam/readIdentityadvantage0/biases/bias/Adam*
T0*)
_class
loc:@advantage0/biases/bias

/advantage0/biases/bias/Adam_1/Initializer/zerosConst*
valueB*    *)
_class
loc:@advantage0/biases/bias*
dtype0

advantage0/biases/bias/Adam_1
VariableV2*
	container *
shape:*
shared_name *)
_class
loc:@advantage0/biases/bias*
dtype0
Û
$advantage0/biases/bias/Adam_1/AssignAssignadvantage0/biases/bias/Adam_1/advantage0/biases/bias/Adam_1/Initializer/zeros*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(

"advantage0/biases/bias/Adam_1/readIdentityadvantage0/biases/bias/Adam_1*
T0*)
_class
loc:@advantage0/biases/bias

0advantage1/weights/weight/Adam/Initializer/zerosConst*
valueB*    *,
_class"
 loc:@advantage1/weights/weight*
dtype0
 
advantage1/weights/weight/Adam
VariableV2*
dtype0*
	container *
shape
:*
shared_name *,
_class"
 loc:@advantage1/weights/weight
á
%advantage1/weights/weight/Adam/AssignAssignadvantage1/weights/weight/Adam0advantage1/weights/weight/Adam/Initializer/zeros*
use_locking(*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(

#advantage1/weights/weight/Adam/readIdentityadvantage1/weights/weight/Adam*
T0*,
_class"
 loc:@advantage1/weights/weight

2advantage1/weights/weight/Adam_1/Initializer/zerosConst*
valueB*    *,
_class"
 loc:@advantage1/weights/weight*
dtype0
¢
 advantage1/weights/weight/Adam_1
VariableV2*
shared_name *,
_class"
 loc:@advantage1/weights/weight*
dtype0*
	container *
shape
:
ç
'advantage1/weights/weight/Adam_1/AssignAssign advantage1/weights/weight/Adam_12advantage1/weights/weight/Adam_1/Initializer/zeros*
use_locking(*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(

%advantage1/weights/weight/Adam_1/readIdentity advantage1/weights/weight/Adam_1*
T0*,
_class"
 loc:@advantage1/weights/weight

-advantage1/biases/bias/Adam/Initializer/zerosConst*
dtype0*
valueB*    *)
_class
loc:@advantage1/biases/bias

advantage1/biases/bias/Adam
VariableV2*
dtype0*
	container *
shape:*
shared_name *)
_class
loc:@advantage1/biases/bias
Õ
"advantage1/biases/bias/Adam/AssignAssignadvantage1/biases/bias/Adam-advantage1/biases/bias/Adam/Initializer/zeros*
use_locking(*
T0*)
_class
loc:@advantage1/biases/bias*
validate_shape(
}
 advantage1/biases/bias/Adam/readIdentityadvantage1/biases/bias/Adam*
T0*)
_class
loc:@advantage1/biases/bias

/advantage1/biases/bias/Adam_1/Initializer/zerosConst*
dtype0*
valueB*    *)
_class
loc:@advantage1/biases/bias

advantage1/biases/bias/Adam_1
VariableV2*
dtype0*
	container *
shape:*
shared_name *)
_class
loc:@advantage1/biases/bias
Û
$advantage1/biases/bias/Adam_1/AssignAssignadvantage1/biases/bias/Adam_1/advantage1/biases/bias/Adam_1/Initializer/zeros*)
_class
loc:@advantage1/biases/bias*
validate_shape(*
use_locking(*
T0

"advantage1/biases/bias/Adam_1/readIdentityadvantage1/biases/bias/Adam_1*
T0*)
_class
loc:@advantage1/biases/bias
@
train/learning_rateConst*
valueB
 *o:*
dtype0
8
train/beta1Const*
valueB
 *fff?*
dtype0
8
train/beta2Const*
valueB
 *w¾?*
dtype0
:
train/epsilonConst*
valueB
 *wÌ+2*
dtype0
Å
6train/update_fully_connected0/weights/weight/ApplyAdam	ApplyAdamfully_connected0/weights/weight$fully_connected0/weights/weight/Adam&fully_connected0/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonKgradients/fully_connected0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
use_nesterov( *
use_locking( 
³
3train/update_fully_connected0/biases/bias/ApplyAdam	ApplyAdamfully_connected0/biases/bias!fully_connected0/biases/bias/Adam#fully_connected0/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonHgradients/fully_connected0/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_locking( *
T0*/
_class%
#!loc:@fully_connected0/biases/bias*
use_nesterov( 
Å
6train/update_fully_connected1/weights/weight/ApplyAdam	ApplyAdamfully_connected1/weights/weight$fully_connected1/weights/weight/Adam&fully_connected1/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonKgradients/fully_connected1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*2
_class(
&$loc:@fully_connected1/weights/weight*
use_nesterov( *
use_locking( *
T0
³
3train/update_fully_connected1/biases/bias/ApplyAdam	ApplyAdamfully_connected1/biases/bias!fully_connected1/biases/bias/Adam#fully_connected1/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonHgradients/fully_connected1/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_nesterov( *
use_locking( *
T0*/
_class%
#!loc:@fully_connected1/biases/bias

,train/update_value0/weights/weight/ApplyAdam	ApplyAdamvalue0/weights/weightvalue0/weights/weight/Adamvalue0/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonAgradients/value0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*
use_locking( *
T0*(
_class
loc:@value0/weights/weight*
use_nesterov( 
÷
)train/update_value0/biases/bias/ApplyAdam	ApplyAdamvalue0/biases/biasvalue0/biases/bias/Adamvalue0/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilon>gradients/value0/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_locking( *
T0*%
_class
loc:@value0/biases/bias*
use_nesterov( 

,train/update_value1/weights/weight/ApplyAdam	ApplyAdamvalue1/weights/weightvalue1/weights/weight/Adamvalue1/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonAgradients/value1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*
use_locking( *
T0*(
_class
loc:@value1/weights/weight*
use_nesterov( 
÷
)train/update_value1/biases/bias/ApplyAdam	ApplyAdamvalue1/biases/biasvalue1/biases/bias/Adamvalue1/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilon>gradients/value1/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_locking( *
T0*%
_class
loc:@value1/biases/bias*
use_nesterov( 
¡
0train/update_advantage0/weights/weight/ApplyAdam	ApplyAdamadvantage0/weights/weightadvantage0/weights/weight/Adam advantage0/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonEgradients/advantage0/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*
use_locking( *
T0*,
_class"
 loc:@advantage0/weights/weight*
use_nesterov( 

-train/update_advantage0/biases/bias/ApplyAdam	ApplyAdamadvantage0/biases/biasadvantage0/biases/bias/Adamadvantage0/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonBgradients/advantage0/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_nesterov( *
use_locking( *
T0*)
_class
loc:@advantage0/biases/bias
¡
0train/update_advantage1/weights/weight/ApplyAdam	ApplyAdamadvantage1/weights/weightadvantage1/weights/weight/Adam advantage1/weights/weight/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonEgradients/advantage1/Wx_plus_b/MatMul_grad/tuple/control_dependency_1*
use_locking( *
T0*,
_class"
 loc:@advantage1/weights/weight*
use_nesterov( 

-train/update_advantage1/biases/bias/ApplyAdam	ApplyAdamadvantage1/biases/biasadvantage1/biases/bias/Adamadvantage1/biases/bias/Adam_1beta1_power/readbeta2_power/readtrain/learning_ratetrain/beta1train/beta2train/epsilonBgradients/advantage1/Wx_plus_b/add_grad/tuple/control_dependency_1*
use_locking( *
T0*)
_class
loc:@advantage1/biases/bias*
use_nesterov( 
½
	train/mulMulbeta1_power/readtrain/beta1.^train/update_advantage0/biases/bias/ApplyAdam1^train/update_advantage0/weights/weight/ApplyAdam.^train/update_advantage1/biases/bias/ApplyAdam1^train/update_advantage1/weights/weight/ApplyAdam4^train/update_fully_connected0/biases/bias/ApplyAdam7^train/update_fully_connected0/weights/weight/ApplyAdam4^train/update_fully_connected1/biases/bias/ApplyAdam7^train/update_fully_connected1/weights/weight/ApplyAdam*^train/update_value0/biases/bias/ApplyAdam-^train/update_value0/weights/weight/ApplyAdam*^train/update_value1/biases/bias/ApplyAdam-^train/update_value1/weights/weight/ApplyAdam*
T0*)
_class
loc:@advantage0/biases/bias

train/AssignAssignbeta1_power	train/mul*
use_locking( *
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(
¿
train/mul_1Mulbeta2_power/readtrain/beta2.^train/update_advantage0/biases/bias/ApplyAdam1^train/update_advantage0/weights/weight/ApplyAdam.^train/update_advantage1/biases/bias/ApplyAdam1^train/update_advantage1/weights/weight/ApplyAdam4^train/update_fully_connected0/biases/bias/ApplyAdam7^train/update_fully_connected0/weights/weight/ApplyAdam4^train/update_fully_connected1/biases/bias/ApplyAdam7^train/update_fully_connected1/weights/weight/ApplyAdam*^train/update_value0/biases/bias/ApplyAdam-^train/update_value0/weights/weight/ApplyAdam*^train/update_value1/biases/bias/ApplyAdam-^train/update_value1/weights/weight/ApplyAdam*
T0*)
_class
loc:@advantage0/biases/bias

train/Assign_1Assignbeta2_powertrain/mul_1*
use_locking( *
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(

trainNoOp^train/Assign^train/Assign_1.^train/update_advantage0/biases/bias/ApplyAdam1^train/update_advantage0/weights/weight/ApplyAdam.^train/update_advantage1/biases/bias/ApplyAdam1^train/update_advantage1/weights/weight/ApplyAdam4^train/update_fully_connected0/biases/bias/ApplyAdam7^train/update_fully_connected0/weights/weight/ApplyAdam4^train/update_fully_connected1/biases/bias/ApplyAdam7^train/update_fully_connected1/weights/weight/ApplyAdam*^train/update_value0/biases/bias/ApplyAdam-^train/update_value0/weights/weight/ApplyAdam*^train/update_value1/biases/bias/ApplyAdam-^train/update_value1/weights/weight/ApplyAdam

initNoOp#^advantage0/biases/bias/Adam/Assign%^advantage0/biases/bias/Adam_1/Assign^advantage0/biases/bias/Assign&^advantage0/weights/weight/Adam/Assign(^advantage0/weights/weight/Adam_1/Assign!^advantage0/weights/weight/Assign#^advantage1/biases/bias/Adam/Assign%^advantage1/biases/bias/Adam_1/Assign^advantage1/biases/bias/Assign&^advantage1/weights/weight/Adam/Assign(^advantage1/weights/weight/Adam_1/Assign!^advantage1/weights/weight/Assign^beta1_power/Assign^beta2_power/Assign)^fully_connected0/biases/bias/Adam/Assign+^fully_connected0/biases/bias/Adam_1/Assign$^fully_connected0/biases/bias/Assign,^fully_connected0/weights/weight/Adam/Assign.^fully_connected0/weights/weight/Adam_1/Assign'^fully_connected0/weights/weight/Assign)^fully_connected1/biases/bias/Adam/Assign+^fully_connected1/biases/bias/Adam_1/Assign$^fully_connected1/biases/bias/Assign,^fully_connected1/weights/weight/Adam/Assign.^fully_connected1/weights/weight/Adam_1/Assign'^fully_connected1/weights/weight/Assign^value0/biases/bias/Adam/Assign!^value0/biases/bias/Adam_1/Assign^value0/biases/bias/Assign"^value0/weights/weight/Adam/Assign$^value0/weights/weight/Adam_1/Assign^value0/weights/weight/Assign^value1/biases/bias/Adam/Assign!^value1/biases/bias/Adam_1/Assign^value1/biases/bias/Assign"^value1/weights/weight/Adam/Assign$^value1/weights/weight/Adam_1/Assign^value1/weights/weight/Assign
A
save/filename/inputConst*
valueB Bmodel*
dtype0
V
save/filenamePlaceholderWithDefaultsave/filename/input*
shape: *
dtype0
M

save/ConstPlaceholderWithDefaultsave/filename*
dtype0*
shape: 
	
save/SaveV2/tensor_namesConst*í
valueãBà&Badvantage0/biases/biasBadvantage0/biases/bias/AdamBadvantage0/biases/bias/Adam_1Badvantage0/weights/weightBadvantage0/weights/weight/AdamB advantage0/weights/weight/Adam_1Badvantage1/biases/biasBadvantage1/biases/bias/AdamBadvantage1/biases/bias/Adam_1Badvantage1/weights/weightBadvantage1/weights/weight/AdamB advantage1/weights/weight/Adam_1Bbeta1_powerBbeta2_powerBfully_connected0/biases/biasB!fully_connected0/biases/bias/AdamB#fully_connected0/biases/bias/Adam_1Bfully_connected0/weights/weightB$fully_connected0/weights/weight/AdamB&fully_connected0/weights/weight/Adam_1Bfully_connected1/biases/biasB!fully_connected1/biases/bias/AdamB#fully_connected1/biases/bias/Adam_1Bfully_connected1/weights/weightB$fully_connected1/weights/weight/AdamB&fully_connected1/weights/weight/Adam_1Bvalue0/biases/biasBvalue0/biases/bias/AdamBvalue0/biases/bias/Adam_1Bvalue0/weights/weightBvalue0/weights/weight/AdamBvalue0/weights/weight/Adam_1Bvalue1/biases/biasBvalue1/biases/bias/AdamBvalue1/biases/bias/Adam_1Bvalue1/weights/weightBvalue1/weights/weight/AdamBvalue1/weights/weight/Adam_1*
dtype0

save/SaveV2/shape_and_slicesConst*_
valueVBT&B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B *
dtype0
ç	
save/SaveV2SaveV2
save/Constsave/SaveV2/tensor_namessave/SaveV2/shape_and_slicesadvantage0/biases/biasadvantage0/biases/bias/Adamadvantage0/biases/bias/Adam_1advantage0/weights/weightadvantage0/weights/weight/Adam advantage0/weights/weight/Adam_1advantage1/biases/biasadvantage1/biases/bias/Adamadvantage1/biases/bias/Adam_1advantage1/weights/weightadvantage1/weights/weight/Adam advantage1/weights/weight/Adam_1beta1_powerbeta2_powerfully_connected0/biases/bias!fully_connected0/biases/bias/Adam#fully_connected0/biases/bias/Adam_1fully_connected0/weights/weight$fully_connected0/weights/weight/Adam&fully_connected0/weights/weight/Adam_1fully_connected1/biases/bias!fully_connected1/biases/bias/Adam#fully_connected1/biases/bias/Adam_1fully_connected1/weights/weight$fully_connected1/weights/weight/Adam&fully_connected1/weights/weight/Adam_1value0/biases/biasvalue0/biases/bias/Adamvalue0/biases/bias/Adam_1value0/weights/weightvalue0/weights/weight/Adamvalue0/weights/weight/Adam_1value1/biases/biasvalue1/biases/bias/Adamvalue1/biases/bias/Adam_1value1/weights/weightvalue1/weights/weight/Adamvalue1/weights/weight/Adam_1*4
dtypes*
(2&
e
save/control_dependencyIdentity
save/Const^save/SaveV2*
_class
loc:@save/Const*
T0
°	
save/RestoreV2/tensor_namesConst"/device:CPU:0*í
valueãBà&Badvantage0/biases/biasBadvantage0/biases/bias/AdamBadvantage0/biases/bias/Adam_1Badvantage0/weights/weightBadvantage0/weights/weight/AdamB advantage0/weights/weight/Adam_1Badvantage1/biases/biasBadvantage1/biases/bias/AdamBadvantage1/biases/bias/Adam_1Badvantage1/weights/weightBadvantage1/weights/weight/AdamB advantage1/weights/weight/Adam_1Bbeta1_powerBbeta2_powerBfully_connected0/biases/biasB!fully_connected0/biases/bias/AdamB#fully_connected0/biases/bias/Adam_1Bfully_connected0/weights/weightB$fully_connected0/weights/weight/AdamB&fully_connected0/weights/weight/Adam_1Bfully_connected1/biases/biasB!fully_connected1/biases/bias/AdamB#fully_connected1/biases/bias/Adam_1Bfully_connected1/weights/weightB$fully_connected1/weights/weight/AdamB&fully_connected1/weights/weight/Adam_1Bvalue0/biases/biasBvalue0/biases/bias/AdamBvalue0/biases/bias/Adam_1Bvalue0/weights/weightBvalue0/weights/weight/AdamBvalue0/weights/weight/Adam_1Bvalue1/biases/biasBvalue1/biases/bias/AdamBvalue1/biases/bias/Adam_1Bvalue1/weights/weightBvalue1/weights/weight/AdamBvalue1/weights/weight/Adam_1*
dtype0
¥
save/RestoreV2/shape_and_slicesConst"/device:CPU:0*_
valueVBT&B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B *
dtype0
ª
save/RestoreV2	RestoreV2
save/Constsave/RestoreV2/tensor_namessave/RestoreV2/shape_and_slices"/device:CPU:0*4
dtypes*
(2&

save/AssignAssignadvantage0/biases/biassave/RestoreV2*)
_class
loc:@advantage0/biases/bias*
validate_shape(*
use_locking(*
T0
£
save/Assign_1Assignadvantage0/biases/bias/Adamsave/RestoreV2:1*
validate_shape(*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias
¥
save/Assign_2Assignadvantage0/biases/bias/Adam_1save/RestoreV2:2*
validate_shape(*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias
¤
save/Assign_3Assignadvantage0/weights/weightsave/RestoreV2:3*
use_locking(*
T0*,
_class"
 loc:@advantage0/weights/weight*
validate_shape(
©
save/Assign_4Assignadvantage0/weights/weight/Adamsave/RestoreV2:4*
use_locking(*
T0*,
_class"
 loc:@advantage0/weights/weight*
validate_shape(
«
save/Assign_5Assign advantage0/weights/weight/Adam_1save/RestoreV2:5*,
_class"
 loc:@advantage0/weights/weight*
validate_shape(*
use_locking(*
T0

save/Assign_6Assignadvantage1/biases/biassave/RestoreV2:6*
use_locking(*
T0*)
_class
loc:@advantage1/biases/bias*
validate_shape(
£
save/Assign_7Assignadvantage1/biases/bias/Adamsave/RestoreV2:7*
use_locking(*
T0*)
_class
loc:@advantage1/biases/bias*
validate_shape(
¥
save/Assign_8Assignadvantage1/biases/bias/Adam_1save/RestoreV2:8*
use_locking(*
T0*)
_class
loc:@advantage1/biases/bias*
validate_shape(
¤
save/Assign_9Assignadvantage1/weights/weightsave/RestoreV2:9*
use_locking(*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(
«
save/Assign_10Assignadvantage1/weights/weight/Adamsave/RestoreV2:10*
use_locking(*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(
­
save/Assign_11Assign advantage1/weights/weight/Adam_1save/RestoreV2:11*
T0*,
_class"
 loc:@advantage1/weights/weight*
validate_shape(*
use_locking(

save/Assign_12Assignbeta1_powersave/RestoreV2:12*
use_locking(*
T0*)
_class
loc:@advantage0/biases/bias*
validate_shape(

save/Assign_13Assignbeta2_powersave/RestoreV2:13*)
_class
loc:@advantage0/biases/bias*
validate_shape(*
use_locking(*
T0
¬
save/Assign_14Assignfully_connected0/biases/biassave/RestoreV2:14*
use_locking(*
T0*/
_class%
#!loc:@fully_connected0/biases/bias*
validate_shape(
±
save/Assign_15Assign!fully_connected0/biases/bias/Adamsave/RestoreV2:15*/
_class%
#!loc:@fully_connected0/biases/bias*
validate_shape(*
use_locking(*
T0
³
save/Assign_16Assign#fully_connected0/biases/bias/Adam_1save/RestoreV2:16*
use_locking(*
T0*/
_class%
#!loc:@fully_connected0/biases/bias*
validate_shape(
²
save/Assign_17Assignfully_connected0/weights/weightsave/RestoreV2:17*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(
·
save/Assign_18Assign$fully_connected0/weights/weight/Adamsave/RestoreV2:18*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(
¹
save/Assign_19Assign&fully_connected0/weights/weight/Adam_1save/RestoreV2:19*
use_locking(*
T0*2
_class(
&$loc:@fully_connected0/weights/weight*
validate_shape(
¬
save/Assign_20Assignfully_connected1/biases/biassave/RestoreV2:20*
validate_shape(*
use_locking(*
T0*/
_class%
#!loc:@fully_connected1/biases/bias
±
save/Assign_21Assign!fully_connected1/biases/bias/Adamsave/RestoreV2:21*
use_locking(*
T0*/
_class%
#!loc:@fully_connected1/biases/bias*
validate_shape(
³
save/Assign_22Assign#fully_connected1/biases/bias/Adam_1save/RestoreV2:22*/
_class%
#!loc:@fully_connected1/biases/bias*
validate_shape(*
use_locking(*
T0
²
save/Assign_23Assignfully_connected1/weights/weightsave/RestoreV2:23*
validate_shape(*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight
·
save/Assign_24Assign$fully_connected1/weights/weight/Adamsave/RestoreV2:24*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight*
validate_shape(
¹
save/Assign_25Assign&fully_connected1/weights/weight/Adam_1save/RestoreV2:25*
use_locking(*
T0*2
_class(
&$loc:@fully_connected1/weights/weight*
validate_shape(

save/Assign_26Assignvalue0/biases/biassave/RestoreV2:26*
validate_shape(*
use_locking(*
T0*%
_class
loc:@value0/biases/bias

save/Assign_27Assignvalue0/biases/bias/Adamsave/RestoreV2:27*
use_locking(*
T0*%
_class
loc:@value0/biases/bias*
validate_shape(

save/Assign_28Assignvalue0/biases/bias/Adam_1save/RestoreV2:28*
use_locking(*
T0*%
_class
loc:@value0/biases/bias*
validate_shape(

save/Assign_29Assignvalue0/weights/weightsave/RestoreV2:29*
use_locking(*
T0*(
_class
loc:@value0/weights/weight*
validate_shape(
£
save/Assign_30Assignvalue0/weights/weight/Adamsave/RestoreV2:30*
T0*(
_class
loc:@value0/weights/weight*
validate_shape(*
use_locking(
¥
save/Assign_31Assignvalue0/weights/weight/Adam_1save/RestoreV2:31*
T0*(
_class
loc:@value0/weights/weight*
validate_shape(*
use_locking(

save/Assign_32Assignvalue1/biases/biassave/RestoreV2:32*%
_class
loc:@value1/biases/bias*
validate_shape(*
use_locking(*
T0

save/Assign_33Assignvalue1/biases/bias/Adamsave/RestoreV2:33*
validate_shape(*
use_locking(*
T0*%
_class
loc:@value1/biases/bias

save/Assign_34Assignvalue1/biases/bias/Adam_1save/RestoreV2:34*%
_class
loc:@value1/biases/bias*
validate_shape(*
use_locking(*
T0

save/Assign_35Assignvalue1/weights/weightsave/RestoreV2:35*
use_locking(*
T0*(
_class
loc:@value1/weights/weight*
validate_shape(
£
save/Assign_36Assignvalue1/weights/weight/Adamsave/RestoreV2:36*
use_locking(*
T0*(
_class
loc:@value1/weights/weight*
validate_shape(
¥
save/Assign_37Assignvalue1/weights/weight/Adam_1save/RestoreV2:37*
use_locking(*
T0*(
_class
loc:@value1/weights/weight*
validate_shape(

save/restore_allNoOp^save/Assign^save/Assign_1^save/Assign_10^save/Assign_11^save/Assign_12^save/Assign_13^save/Assign_14^save/Assign_15^save/Assign_16^save/Assign_17^save/Assign_18^save/Assign_19^save/Assign_2^save/Assign_20^save/Assign_21^save/Assign_22^save/Assign_23^save/Assign_24^save/Assign_25^save/Assign_26^save/Assign_27^save/Assign_28^save/Assign_29^save/Assign_3^save/Assign_30^save/Assign_31^save/Assign_32^save/Assign_33^save/Assign_34^save/Assign_35^save/Assign_36^save/Assign_37^save/Assign_4^save/Assign_5^save/Assign_6^save/Assign_7^save/Assign_8^save/Assign_9"