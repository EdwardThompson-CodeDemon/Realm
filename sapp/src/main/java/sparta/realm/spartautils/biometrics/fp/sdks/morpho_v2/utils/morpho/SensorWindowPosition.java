package sparta.realm.spartautils.biometrics.fp.sdks.morpho_v2.utils.morpho;

/**
 * Created by othomas on 30/07/2017.
 */

public enum SensorWindowPosition
{
    Normal_0("0\u00b0", 0), Normal_180("180\u00b0", 1), Reverse_0("0\u00b0-Non Oriented Matching", 2), Reverse_180("180\u00b0-Non Oriented Matching", 3);

    private int		code;
    private String label;

    public int getCode()
    {
        return code;
    }

    public String getLabel()
    {
        return label;
    }

    private SensorWindowPosition(String label, int code)
    {
        this.code = code;
        this.label = label;
    }

    public static SensorWindowPosition fromString(String label)
    {
        if (label != null)
        {
            for (SensorWindowPosition b : SensorWindowPosition.values())
            {
                if (label.equalsIgnoreCase(b.label))
                {
                    return b;
                }
            }
        }
        return null;
    }

}
