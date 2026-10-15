// Avatar dropdown menu handler
document.addEventListener('DOMContentLoaded', function() {
  const avatarTrigger = document.getElementById('avatar-trigger');
  const avatarMenu = document.getElementById('avatar-menu');

  if (!avatarTrigger || !avatarMenu) return;

  let isOpen = false;
  let focusedItemIndex = -1;

  function openDropdown() {
    isOpen = true;
    avatarMenu.classList.add('open');
    focusedItemIndex = -1;
  }

  function closeDropdown() {
    isOpen = false;
    avatarMenu.classList.remove('open');
    focusedItemIndex = -1;
    avatarTrigger.focus();
  }

  function toggleDropdown(e) {
    e.stopPropagation();
    if (isOpen) {
      closeDropdown();
    } else {
      openDropdown();
    }
  }

  function getMenuItems() {
    return Array.from(avatarMenu.querySelectorAll('a, button'));
  }

  function focusMenuItem(index) {
    const items = getMenuItems();
    if (index >= 0 && index < items.length) {
      focusedItemIndex = index;
      items[index].focus();
    }
  }

  function handleKeyDown(e) {
    if (!isOpen) {
      if (e.key === 'Enter' || e.key === ' ') {
        e.preventDefault();
        openDropdown();
        focusMenuItem(0);
      }
      return;
    }

    const items = getMenuItems();
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        focusMenuItem(Math.min(focusedItemIndex + 1, items.length - 1));
        break;
      case 'ArrowUp':
        e.preventDefault();
        focusMenuItem(Math.max(focusedItemIndex - 1, 0));
        break;
      case 'Enter':
      case ' ':
        e.preventDefault();
        if (focusedItemIndex >= 0) {
          items[focusedItemIndex].click();
        }
        break;
      case 'Escape':
        e.preventDefault();
        closeDropdown();
        break;
      case 'Tab':
        closeDropdown();
        break;
      default:
        break;
    }
  }

  // Avatar click to toggle dropdown
  avatarTrigger.addEventListener('click', toggleDropdown);
  avatarTrigger.addEventListener('keydown', handleKeyDown);

  // Close on outside click
  document.addEventListener('click', function(e) {
    if (!avatarTrigger.contains(e.target) && !avatarMenu.contains(e.target)) {
      if (isOpen) closeDropdown();
    }
  });

  // Close on Escape globally
  document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape' && isOpen) {
      closeDropdown();
    }
  });

  // Menu item keyboard navigation
  const menuItems = getMenuItems();
  menuItems.forEach((item, index) => {
    item.addEventListener('keydown', function(e) {
      if (e.key === 'ArrowDown' && index < menuItems.length - 1) {
        e.preventDefault();
        focusedItemIndex = index + 1;
        menuItems[focusedItemIndex].focus();
      } else if (e.key === 'ArrowUp' && index > 0) {
        e.preventDefault();
        focusedItemIndex = index - 1;
        menuItems[focusedItemIndex].focus();
      } else if (e.key === 'Escape') {
        e.preventDefault();
        closeDropdown();
      }
    });

    item.addEventListener('click', function() {
      closeDropdown();
    });
  });
});
