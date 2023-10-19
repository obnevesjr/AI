public class CircularBuffer<ElementType> {

	private final static int DEFAULT_INITIAL_CAPACITY = 20;

	private int capacity;

	private  ElementType[] elements;

	private volatile int readIndex;

	private volatile int writeIndex;

	public CircularBuffer(int capacity) {
		this.capacity = (capacity > 1) ? capacity : DEFAULT_INITIAL_CAPACITY;
		this.elements = (ElementType[]) new Object[this.capacity];
		this.readIndex = 0;
		this.writeIndex = -1;
	}

	public boolean addElement(ElementType element) {
		if (bufferIsNotCurrentlyFull()) {
			int newIndex = writeIndex + 1;
			elements[newIndex % capacity] = element;
			writeIndex++;
			return true;
		}
		
		return false;
	}

	public ElementType getNextElement() {
		if (bufferIsNotCurrentlyEmpty()) {
			ElementType element = elements[readIndex % capacity];
			readIndex++;
			return element;
		}
		
		return null;
	}

	private boolean bufferIsCurrentlyFull() {
		return (writeIndex - readIndex) + 1 == capacity;
	}

	private boolean bufferIsNotCurrentlyFull() {
		return !bufferIsCurrentlyFull();
	}

	private boolean bufferIsCurrentlyEmpty() {
		return writeIndex < readIndex;
	}

	private boolean bufferIsNotCurrentlyEmpty() {
		return !bufferIsCurrentlyEmpty();
	}

}
