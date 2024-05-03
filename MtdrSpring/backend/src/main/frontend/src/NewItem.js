import React, { useState } from "react";
import Button from '@mui/material/Button';

function NewItem(props) {
  const [item, setItem] = useState('');

  function handleSubmit(e) {
    if (!item.trim()) {
      return;
    }
    // Verificar si el texto comienza con "add"
    const message = item.toLowerCase();
    if (!message.startsWith('add')) {
      // El texto no comienza con "add", no se agrega ningún elemento
      return;
    }
    // Quitar la parte "add" del mensaje
    const newItem = item.substring(3).trim();
    // addItem hace la llamada a la API REST:
    props.addItem(newItem);
    setItem("");
    e.preventDefault();
  }

  function handleChange(e) {
    setItem(e.target.value);
  }

  return (
    <div id="newinputform">
      <form>
        <input
          id="newiteminput"
          placeholder="New item"
          type="text"
          autoComplete="off"
          value={item}
          onChange={handleChange}
          // No need to click on the "ADD" button to add a todo item. You
          // can simply press "Enter":
          onKeyDown={event => {
            if (event.key === 'Enter') {
              handleSubmit(event);
            }
          }}
        />
        <span>&nbsp;&nbsp;</span>
        <Button
          className="AddButton"
          variant="contained"
          disabled={props.isInserting}
          onClick={!props.isInserting ? handleSubmit : null}
          size="small"
        >
          {props.isInserting ? 'Adding…' : 'Add'}
        </Button>
      </form>
    </div>
  );
}

export default NewItem;
