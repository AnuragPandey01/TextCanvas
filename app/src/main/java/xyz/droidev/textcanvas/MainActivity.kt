package xyz.droidev.textcanvas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.droidev.textcanvas.ui.theme.TextCanvasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TextCanvasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {

    val viewModel: MainViewModel = viewModel()
    val selectedTextItem by viewModel.selectedTextItem.collectAsState()
    val textItems by viewModel.textItems.collectAsState()
    val isUndoStackEmpty by viewModel.isUndoStackEmpty.collectAsState()
    val isRedoStackEmpty by viewModel.isRedoStackEmpty.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ){

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            IconButton(
                onClick = viewModel::undo,
                enabled = !isUndoStackEmpty
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_undo), contentDescription = "undo")
            }
            IconButton(
                onClick = viewModel::redo,
                enabled = !isRedoStackEmpty
            ) {
                Icon(ImageVector.vectorResource(R.drawable.ic_redo), contentDescription = "redo")
            }
        }
        Box(
            modifier = Modifier.weight(1f)
        ){

            // Text Canvas
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
                    .clickable {
                        viewModel.deselectTextItem()
                    }
            )

            textItems.forEach { currentItem ->
                Text(
                    currentItem.text,
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                currentItem.offset.x.toInt(),
                                currentItem.offset.y.toInt()
                            )
                        }
                        .clickable {
                            viewModel.selectTextItem(currentItem)
                        },

                    style = TextStyle.Default.copy(
                        fontSize = currentItem.fontSize.sp,
                        fontWeight = if(currentItem.isBold) FontWeight.Bold else FontWeight.Normal,
                        fontStyle = if(currentItem.isItalic) FontStyle.Italic else FontStyle.Normal,
                        textDecoration = if(currentItem.isUnderline) TextDecoration.Underline else TextDecoration.None,
                        color = Color.Black
                    )
                )
            }

            selectedTextItem?.let{
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                it.offset.x.toInt(),
                                it.offset.y.toInt()
                            )
                        }
                        .pointerInput(Unit){
                            detectTransformGestures { _, pan, _, _ ->
                                viewModel.updateSelectedTextItemOffset(pan)
                            }
                            detectDragGestures(
                                onDrag = { _,_ ->  },
                                onDragEnd = viewModel::dragFinished
                            )
                        }.border(1.dp,Color.Gray)
                        .padding(4.dp)
                ){
                    BasicTextField(
                        value = it.text,
                        onValueChange = viewModel::updateSelectedTextItemText,
                        textStyle = TextStyle.Default.copy(
                            fontSize = it.fontSize.sp,
                            fontWeight = if(it.isBold) FontWeight.Bold else FontWeight.Normal,
                            fontStyle = if(it.isItalic) FontStyle.Italic else FontStyle.Normal,
                            textDecoration = if(it.isUnderline) TextDecoration.Underline else TextDecoration.None,
                            color = Color.Black
                        ),
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }


        Row{
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                TextButton(
                    onClick = viewModel::decreaseFont
                ) { Text("-", fontSize = 18.sp)}
                Text(selectedTextItem?.fontSize?.toString() ?: "10")
                TextButton(
                    onClick = viewModel::increaseFont
                ) { Text("+", fontSize = 18.sp)}
            }
            IconButton(
                onClick = viewModel::toggleBold,
                colors = if(selectedTextItem == null || !selectedTextItem!!.isBold){
                    IconButtonDefaults.iconButtonColors()
                }else{
                    IconButtonDefaults.filledIconButtonColors()
                },
                enabled = selectedTextItem != null
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_bold),
                    contentDescription = "Bold"
                )
            }
            IconButton(
                onClick = viewModel::toggleItalic,
                colors = if(selectedTextItem == null || !selectedTextItem!!.isItalic){
                    IconButtonDefaults.iconButtonColors()
                }else{
                    IconButtonDefaults.filledIconButtonColors()
                },
                enabled = selectedTextItem != null
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_italic),
                    contentDescription = "italic"
                )
            }
            IconButton(
                onClick = viewModel::toggleUnderline,
                colors = if(selectedTextItem == null || !selectedTextItem!!.isUnderline){
                    IconButtonDefaults.iconButtonColors()
                }else{
                    IconButtonDefaults.filledIconButtonColors()
                },
                enabled = selectedTextItem != null
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.ic_underline),
                    contentDescription = "underline"
                )
            }
        }


        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = viewModel::addNewTextItem
        ) {
            Text("Add Text")
        }
    }

}


