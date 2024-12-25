package xyz.droidev.textcanvas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Stack

/**
 * Project : Text Canvas.
 * @author PANDEY ANURAG.
 */

class MainViewModel : ViewModel() {

    private val _selectedTextItem = MutableStateFlow<TextItem?>(null)
    val selectedTextItem = _selectedTextItem.asStateFlow()

    private val _textItems = MutableStateFlow<List<TextItem>>(mutableListOf())
    val textItems = _textItems.asStateFlow()

    private val undoStack = Stack<Pair<TextItem?,List<TextItem>>>()
    private val redoStack = Stack<Pair<TextItem?,List<TextItem>>>()

    private val _isUndoStackEmpty = MutableStateFlow(true)
    val isUndoStackEmpty = _isUndoStackEmpty.asStateFlow()

    private val _isRedoStackEmpty = MutableStateFlow(true)
    val isRedoStackEmpty = _isRedoStackEmpty.asStateFlow()

    private fun updateStackStates() {
        _isUndoStackEmpty.value = undoStack.isEmpty()
        _isRedoStackEmpty.value = redoStack.isEmpty()
    }


    fun deselectTextItem(){
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.value?.let{ textItem ->
            _textItems.update{
                it+textItem
            }
        }
        _selectedTextItem.update {
            null
        }
        updateStackStates()
    }

    fun selectTextItem(textItem: TextItem){
        _selectedTextItem.value?.let{ prevItem ->
            _textItems.update{
                it+prevItem
            }
        }
        _selectedTextItem.update {
            textItem
        }
        _textItems.update {
            it-textItem
        }
        updateStackStates()
    }

    fun updateSelectedTextItemOffset(newOffset: Offset){
        _selectedTextItem.update{
            it?.copy(offset = Offset(it.offset.x+newOffset.x,it.offset.y+newOffset.y))
        }
    }

    fun dragFinished(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        updateStackStates()
    }

    fun updateSelectedTextItemText(newText: String){
        _selectedTextItem.update {
            it?.copy(text = newText)
        }
    }

    fun increaseFont(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.update{
            it?.copy(fontSize = it.fontSize+1)
        }
        updateStackStates()
    }

    fun decreaseFont(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.update{
            it?.copy(fontSize = (it.fontSize-1).coerceAtLeast(1))
        }
        updateStackStates()
    }

    fun toggleBold(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.update{
            it?.copy(isBold = !it.isBold)
        }
        updateStackStates()
    }

    fun toggleItalic(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.update{
            it?.copy(isItalic = !it.isItalic)
        }
        updateStackStates()
    }

    fun toggleUnderline(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.update{
            it?.copy(isUnderline = !it.isUnderline)
        }
        updateStackStates()
    }

    fun addNewTextItem(){
        redoStack.clear()
        undoStack.push(Pair(_selectedTextItem.value,_textItems.value))
        _selectedTextItem.value?.let{ textItem ->
            _textItems.update{ it+textItem }
        }
        _selectedTextItem.update{
            TextItem("hello", Offset(100f,100f),16)
        }
        updateStackStates()
    }

    fun undo(){
        if(undoStack.isEmpty()) return
        val top = undoStack.pop()
        redoStack.push(top)
        _selectedTextItem.update{
            top.first
        }
        _textItems.update {
            top.second
        }
        updateStackStates()
    }

    fun redo(){
        if(redoStack.isEmpty()) return
        val top = redoStack.pop()
        undoStack.push(top)
        _selectedTextItem.update{
            top.first
        }
        _textItems.update {
            top.second
        }
        updateStackStates()
    }

}

data class TextItem(
    val text: String,
    val offset: Offset,
    val fontSize: Int,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
    val isUnderline: Boolean = false,
    val fontFamily: FontFamily = FontFamily.Default
)

