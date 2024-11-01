using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class scroll : MonoBehaviour
{
    private SpriteRenderer render;
    [Tooltip("RollSpeed"), Range(1f, 10f)]
    public float speed = 3f;
    // Start is called before the first frame update
    void Start()
    {
        render = GetComponent<SpriteRenderer>();
    }

    private void Roll()
    {
        render.material.mainTextureOffset += new Vector2(speed * Time.deltaTime, 0);
    }

    // Update is called once per frame
    void Update()
    {
        Roll();
        
    }
}
