package com.gladys.cybuverse.CyberBullyingAwarenessGame.Base;

import android.os.CountDownTimer;

import com.gladys.cybuverse.Helpers.Helper;
import com.gladys.cybuverse.Utils.GameBase.EventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

public class MessageGetter extends Message {
    public static final String EXPECTED_CONTENT_TYPE = "--expected-content-info";
    public static final String START_GET_MESSAGE_EVENT = "--start-get-message";
    public static final String WAIT_TIMER_TICK_EVENT = "--wait-timer-tick";
    public static final String WAIT_TIMER_END_EVENT = "--wait-timer-end";
    public static final String WAIT_TIMER_START_EVENT = "--wait-timer-start";
    public static final String MESSAGE_RECEIVED_EVENT = "--message-received";
    public static final String WRONG_MESSAGE_RECEIVED_EVENT = "--wrong-message-received-event";
    public static final String MESSAGE_NOT_RECEIVED_EVENT = "--message-not-received";
    public static final String END_GET_MESSAGE_EVENT = "--end-get-message";

    public static final String TIMEOUT = "--timeout";
    public static final String TIMEOUTS_COUNT = "--timeouts-count";

    public static final String DATA_TIMEOUT = "timeout";
    public static final String DATA_MILLIS = "millis";
    public static final String DATA_CONTENT = "content";
    public static final String DATA_TIMEOUTS_COUNT = "timeout-count";

    private EventListener listener;
    private CountDownTimer timer;
    private ContentValidator contentValidator;
    private List<MessageContent> contentOptions;
    private OnReceiveContentCompleteListener onReceiveContentCompleteListener;
    private TimerEventListener onTimerEventListener;
    private OnInterruptListener onInterruptListener;

    public abstract static class OnReceiveContentCompleteListener {
        public abstract void onSuccess(MessageContent content);
        public void onFailure(MessageContent content){};
    }

    public interface TimerEventListener {
        void onStart(MessageGetter messageGetter);
        void onTick(int timePastMillis, MessageGetter messageGetter);
        void onFinish(int timeoutsCount, MessageGetter messageGetter);
    }

    public interface OnInterruptListener {
        void onInterrupt(Message message, boolean isReadNextMessage);
    }

    public interface ContentValidator {
        boolean isValid(MessageContent content, List<MessageContent> contentOptions);
    }

    public MessageGetter(Actor sender, List<Actor> receivers, Integer timeout, EventListener listener) {
        super(sender, receivers);
        this.listener = listener;
        setTimeout(timeout);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, List<Actor> receivers, EventListener listener) {
        super(sender, receivers);
        this.listener = listener;
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, List<Actor> receivers, Integer timeout) {
        super(sender, receivers);
        setTimeout(timeout);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, List<Actor> receivers) {
        super(sender, receivers);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, Actor receiver, Integer timeout, EventListener listener) {
        super(sender, receiver);
        setTimeout(timeout);
        this.listener = listener;
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, Actor receiver, EventListener listener) {
        super(sender, receiver);
        this.listener = listener;
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, Actor receiver, Integer timeout) {
        super(sender, receiver);
        setTimeout(timeout);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Actor sender, Actor receiver) {
        super(sender, receiver);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Integer timeout, EventListener listener) {
        this.listener = listener;
        setTimeout(timeout);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(EventListener listener) {
        this.listener = listener;
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter(Integer timeout) {
        setTimeout(timeout);
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public MessageGetter() {
        setProperty(EXPECTED_CONTENT_TYPE, MessageContent.TYPE_ALL);
    }

    public OnReceiveContentCompleteListener getOnReceiveContentCompleteListener() {
        return onReceiveContentCompleteListener;
    }

    public MessageGetter setOnReceiveContentCompleteListener(OnReceiveContentCompleteListener onReceiveContentCompleteListener) {
        this.onReceiveContentCompleteListener = onReceiveContentCompleteListener;
        return this;
    }

    public TimerEventListener getOnTimerEventListener() {
        return onTimerEventListener;
    }

    public MessageGetter setOnTimerEventListener(TimerEventListener onTimerEventListener) {
        this.onTimerEventListener = onTimerEventListener;
        return this;
    }

    public OnInterruptListener getOnInterruptListener() {
        return onInterruptListener;
    }

    public void setOnInterruptListener(OnInterruptListener onInterruptListener) {
        this.onInterruptListener = onInterruptListener;
    }

    public CountDownTimer getTimer() {
        return timer;
    }

    @Override
    public MessageGetter setContent(MessageContent content) {
        //TODO: read Thread to get message and wait for message.

        if (isValidContent(content)) {
            super.setContent(content);
            if (onReceiveContentCompleteListener != null)
                onReceiveContentCompleteListener.onSuccess(content);
        } else {
            if (listener != null) {
                EventListener.Event event = new EventListener.Event(WRONG_MESSAGE_RECEIVED_EVENT);
                event.addProperty(DATA_CONTENT, content);
                listener.processEvent(event);
                if (onReceiveContentCompleteListener != null)
                    onReceiveContentCompleteListener.onFailure(content);
            }
        }

        if (timer != null && !isEmptyContent()) {
            Helper.log("Message got: Cancelling timer");
            timer.cancel();
        }

        if (listener != null) {

            if (isEmptyContent()) {
                listener.processEvent(new EventListener.Event(MESSAGE_NOT_RECEIVED_EVENT));
            } else {
                EventListener.Event event = new EventListener.Event(MESSAGE_RECEIVED_EVENT);
                event.addProperty(DATA_CONTENT, getContent());
                listener.processEvent(event);
                listener.processEvent(new EventListener.Event(END_GET_MESSAGE_EVENT));
            }
        }

        return this;
    }

    public String getExpectedContentType() {
        return (String) getProperty(EXPECTED_CONTENT_TYPE);
    }

    public MessageGetter setExpectedContentType(String expectedContentType) {
        setProperty(EXPECTED_CONTENT_TYPE, expectedContentType);
        return this;
    }

    public void waitForMessage() {

        if (listener != null) {
            listener.processEvent(new EventListener.Event(START_GET_MESSAGE_EVENT));
        }

        if (getTimeout() != null) {
            timer = new CountDownTimer(getTimeout(), 1000) {
                @Override
                public void onTick(long l) {

                    if (!isEmptyContent()) {
                        Helper.log("Cancelling timer");
                        cancel();
                    } else if (listener != null) {
                        EventListener.Event event = new EventListener.Event(WAIT_TIMER_TICK_EVENT);
                        event.addProperty(DATA_TIMEOUT, getTimeout());
                        event.addProperty(DATA_MILLIS, l);
                        listener.processEvent(event);
                        if (onTimerEventListener != null)
                            onTimerEventListener.onTick((int) Math.floor(l), MessageGetter.this);
                    }
                }

                @Override
                public void onFinish() {
                    if (listener != null) {
                        incrementTimeoutsCount();
                        EventListener.Event event = new EventListener.Event(WAIT_TIMER_END_EVENT);
                        event.addProperty(DATA_TIMEOUT, getTimeout());
                        event.addProperty(DATA_TIMEOUTS_COUNT, getTimeoutsCount());
                        listener.processEvent(event);
                        if (onTimerEventListener != null)
                            onTimerEventListener.onFinish(getTimeoutsCount(), MessageGetter.this);
                    }
                }
            };

            timer.start();
            if (listener != null) {
                EventListener.Event event = new EventListener.Event(WAIT_TIMER_START_EVENT);
                event.addProperty(TIMEOUT, getTimeout());
                listener.processEvent(event);
                if (onTimerEventListener != null)
                    onTimerEventListener.onStart(MessageGetter.this);
            }
        }
    }

    public void gotoOnTimerEndEvent() {
        if (listener != null) {
            incrementTimeoutsCount();
            EventListener.Event event = new EventListener.Event(WAIT_TIMER_END_EVENT);
            event.addProperty(DATA_TIMEOUT, getTimeout());
            event.addProperty(DATA_TIMEOUTS_COUNT, getTimeoutsCount());
            listener.processEvent(event);
            if (onTimerEventListener != null)
                onTimerEventListener.onFinish(getTimeoutsCount(), MessageGetter.this);
        }
    }

    public boolean isValidContent(MessageContent content) {
        if (contentValidator != null)
            return contentValidator.isValid(content, getContentOptions());
        else return true;
    }

    public ContentValidator getContentValidator() {
        return contentValidator;
    }

    public MessageGetter setContentValidator(ContentValidator contentValidator) {
        this.contentValidator = contentValidator;
        return this;
    }

    public Integer getTimeout() {
        return (Integer) getProperty(TIMEOUT);
    }

    public MessageGetter setTimeout(Integer timeout) {
        if (getOnTimerEventListener() == null){
            setOnTimerEventListener(new DefaultTimerEventListener());
        }
        return setProperty(TIMEOUT, timeout);
    }

    public MessageGetter setTimeout(Integer timeout, TimerEventListener timerEventListener) {
        setProperty(TIMEOUT, timeout);
        if (timerEventListener == null){
            timerEventListener = new DefaultTimerEventListener();
        }
        return setOnTimerEventListener(timerEventListener);
    }

    public Integer getTimeoutsCount() {
        return (getProperty(TIMEOUTS_COUNT) != null) ? (Integer) getProperty(TIMEOUTS_COUNT) : 0;
    }

    public MessageGetter resetTimeoutsCount() {
        return setProperty(TIMEOUTS_COUNT, 0);
    }

    private MessageGetter incrementTimeoutsCount() {
        return setProperty(TIMEOUTS_COUNT, getTimeoutsCount() + 1);
    }

    public EventListener getListener() {
        return listener;
    }

    public MessageGetter setListener(EventListener listener) {
        this.listener = listener;
        return this;
    }

    public List<MessageContent> getContentOptions() {
        return contentOptions;
    }

    public MessageGetter setContentOptions(List<MessageContent> contentOptions) {
        this.contentOptions = contentOptions;
        return this;
    }

    public boolean hasContentOptions() {
        return contentOptions != null && !contentOptions.isEmpty();
    }

    public MessageGetter addContentOptions(MessageContent... messageContent) {
        if (this.contentOptions == null)
            this.contentOptions = new ArrayList<>();
        this.contentOptions.addAll(Arrays.asList(messageContent));
        return this;
    }

    @Override
    public MessageGetter setProperties(Map<String, Object> properties) {
        super.setProperties(properties);
        return this;
    }

    @Override
    public MessageGetter setProperty(String key, Object value) {
        super.setProperty(key, value);
        return this;
    }

    @Override
    public MessageGetter setReceivers(List<Actor> receivers) {
        super.setReceivers(receivers);
        return this;
    }

    @Override
    public MessageGetter setSender(Actor sender) {
        super.setSender(sender);
        return this;
    }

    @Override
    public MessageGetter setOnReadListener(OnReadListener onReadListener) {
        super.setOnReadListener(onReadListener);
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return "MessageGetter<" + getSender() + " => " + getReceivers() + ">";
    }

    public static class DefaultContentValidator implements ContentValidator {
        @Override
        public boolean isValid(MessageContent content, List<MessageContent> contentOptions) {
            if (contentOptions == null || contentOptions.isEmpty()) {
                return true;
            } else {
                for (MessageContent messageContent : contentOptions) {
                    if (messageContent.matches(content)) {
                        return true;
                    }
                }
                return false;
            }
        }
    }

    public static class DefaultTimerEventListener implements TimerEventListener {

        private final TimeoutMessage[] timeoutMessages;
        int i = 0;

        public DefaultTimerEventListener(TimeoutMessage ...timeoutMessages){
            this.timeoutMessages = timeoutMessages;
        }

        @Override
        public void onStart(MessageGetter messageGetter) {
            Helper.log("timer started");
        }

        @Override
        public void onTick(int timePastMillis, MessageGetter messageGetter) {
            Helper.log("timer tick: "+(messageGetter.getTimeout()-timePastMillis));
        }

        @Override
        public void onFinish(int timeoutsCount, MessageGetter messageGetter) {
            Helper.log("Timeout Finished. numberOfTimeOuts: "+i);
            if (messageGetter.isEmptyContent()){
                if (timeoutMessages != null && timeoutMessages.length > 0) {
                    if (i < timeoutMessages.length - 1) {
                        messageGetter.getOnInterruptListener().onInterrupt(timeoutMessages[i].setProperty("owner", messageGetter), false);
                        Helper.log("wait For Message Again");
                        messageGetter.waitForMessage();
                    } else {
                        if (i < timeoutMessages.length) {
                            messageGetter.getOnInterruptListener().onInterrupt(timeoutMessages[i].setProperty("owner", messageGetter), true);
                        } else {
                            messageGetter.getOnInterruptListener().onInterrupt(null, true);
                        }
                    }
                    i++;
                }
                else{
                    messageGetter.getOnInterruptListener().onInterrupt(null, true);
                }
            }
        }
    }

}
