import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.onDrag
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.singleWindowApplication

data class CardAndOffset(val card: Card, val offset: MutableState<Offset> = mutableStateOf(Offset.Zero))

fun MutableList<CardAndOffset>.add(card: Card) = add(CardAndOffset(card))

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
fun main() = singleWindowApplication(
    title = "Playground",
) {
    MaterialTheme(darkColorScheme()) {
        val cardList = remember { mutableStateListOf<CardAndOffset>() }
        Scaffold(
            bottomBar = {
                BottomAppBar {
                    NavigationBarItem(
                        onClick = { cardList.removeLastOrNull() },
                        icon = { Icon(Icons.Default.RemoveCircle, null) },
                        selected = false
                    )
                    NavigationBarItem(
                        onClick = {},
                        icon = { Text(cardList.size.toString()) },
                        selected = false,
                        enabled = false
                    )
                    NavigationBarItem(
                        onClick = { cardList.add(Card.RandomCard) },
                        icon = { Icon(Icons.Default.AddCircle, null) },
                        selected = false
                    )
                }
            }
        ) { padding ->
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {

                var color by remember { mutableStateOf(Color(0, 0, 0)) }
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center)
                        .fillMaxSize()
                        //.background(color = color)
                        .onPointerEvent(PointerEventType.Move) {
                            val position = it.changes.first().position
                            color = Color(position.x.toInt() % 256, 0, position.y.toInt() % 256)
                        }
                )

                /*val m = window.mousePosition ?: Point(0, 0)

                Box(
                    Modifier
                        .offset { IntOffset(m.x - 20, m.y - 40) }
                        .size(40.dp)
                        .border(4.dp, Color.Green, CircleShape)
                )

                Box(
                    Modifier
                        .drag()
                        .size(40.dp)
                        .border(4.dp, Color.Black, CircleShape)
                )*/
            }

            cardList.forEachIndexed { index, card ->
                Box(Modifier.drag(card.offset)) {
                    ContextMenuArea(
                        items = { listOf(ContextMenuItem("Remove") { cardList.remove(card) }) }
                    ) { PlayingCard(card.card) { cardList[index] = cardList[index].copy(card = Card.RandomCard) } }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.drag(
    offset: MutableState<Offset> = remember { mutableStateOf(Offset.Zero) },
    enabled: Boolean = true,
    matcher: PointerMatcher = PointerMatcher.Primary,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) = offset { IntOffset(offset.value.x.toInt(), offset.value.y.toInt()) }
    .onDrag(
        enabled = enabled,
        matcher = matcher,
        onDragStart = onDragStart,
        onDragCancel = onDragCancel,
        onDragEnd = onDragEnd,
    ) { offset.value += it }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Modifier.drag(
    offset: Offset,
    enabled: Boolean = true,
    matcher: PointerMatcher = PointerMatcher.Primary,
    onDragStart: (Offset) -> Unit = {},
    onDragEnd: () -> Unit = {},
    onDragCancel: () -> Unit = {}
) = composed {
    val drag = remember(offset) { mutableStateOf(offset) }
    drag(drag, enabled, matcher, onDragStart, onDragEnd, onDragCancel)
}